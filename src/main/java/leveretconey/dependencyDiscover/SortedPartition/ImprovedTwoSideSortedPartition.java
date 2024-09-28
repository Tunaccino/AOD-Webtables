package leveretconey.dependencyDiscover.SortedPartition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import leveretconey.dependencyDiscover.Validator.Result.ALODValidationResultWithBound;
import leveretconey.dependencyDiscover.Data.DataFrame;
import leveretconey.dependencyDiscover.Dependency.LexicographicalOrderDependency;
import leveretconey.dependencyDiscover.Predicate.SingleAttributePredicate;
import leveretconey.dependencyDiscover.SPCache.SortedPartitionCache;
import leveretconey.dependencyDiscover.Validator.Result.ValidationResultWithAccurateBound;
import leveretconey.util.Statistics;
import leveretconey.util.TimeStatistics;

public class ImprovedTwoSideSortedPartition{
    public static boolean g1IncrementalUseStateCompaction =false;
    public static int segmentTreeThreshold =32;
    public boolean dontUseNull = false;

    public SortedPartition left;
    public SortedPartition right;
    private ALODValidationResultWithBound g1ResultCache;

    public ImprovedTwoSideSortedPartition parent;
    public boolean expandLeft;
    private boolean[] nulls;

    public ImprovedTwoSideSortedPartition(DataFrame data) {
        left=new SortedPartition(data);
        right=new SortedPartition(data);
        intersectNulls();
    }

    private ImprovedTwoSideSortedPartition(){
        intersectNulls();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImprovedTwoSideSortedPartition)) return false;
        ImprovedTwoSideSortedPartition that = (ImprovedTwoSideSortedPartition) o;
        return Objects.equals(left, that.left) &&
                Objects.equals(right, that.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    public ImprovedTwoSideSortedPartition(DataFrame data,
                                          LexicographicalOrderDependency dependency) {
        left=new SortedPartition(data);
        for (SingleAttributePredicate predicate : dependency.left) {
            left.intersect(data,predicate);
        }
        right=new SortedPartition(data);
        for (SingleAttributePredicate predicate : dependency.right) {
            right.intersect(data,predicate);
        }

    }

    public ImprovedTwoSideSortedPartition(LexicographicalOrderDependency dependency
            ,SortedPartitionCache cache){
        left=cache.get(dependency.left);
        right=cache.get(dependency.right);
        intersectNulls();
    }

    public ImprovedTwoSideSortedPartition(SortedPartition left, SortedPartition right) {
        this.left = left;
        this.right = right;
        intersectNulls();
    }

    public ImprovedTwoSideSortedPartition(LexicographicalOrderDependency dependency
            , SortedPartitionCache cache, ImprovedTwoSideSortedPartition parent, boolean expandLeft){
        this(dependency, cache);
        this.parent=parent;
        this.expandLeft=expandLeft;
        intersectNulls();
    }

    private void intersectNulls(){
        boolean[] nulls = new boolean[right.nulls.length];
        for (int i = 0; i < right.nulls.length; i++){
            if(left.nulls[i] || right.nulls[i])
                nulls[i] = true;
        }

        this.nulls = nulls;
    }

    private ImprovedTwoSideSortedPartition deepClone() {
        ImprovedTwoSideSortedPartition result =new ImprovedTwoSideSortedPartition();
        result.left = left.deepClone();
        result.right = right.deepClone();
        return result;
    }

    public ImprovedTwoSideSortedPartition deepCloneAndIntersect
            (DataFrame data, SingleAttributePredicate predicate, boolean intersectLeft) {
        ImprovedTwoSideSortedPartition isp = deepClone();
        isp.parent = this;
        isp.expandLeft = intersectLeft;
        if (intersectLeft) {
            isp.left.intersect(data, predicate);
        } else {
            isp.right.intersect(data, predicate);
        }
        return isp;
    }

    public void removeLonelyTuples(){
        Set<Integer> tuplesToRemove=new HashSet<>();
        SortedPartition smallSp,bigSp;
        if (left.getGroupCount()<right.getGroupCount()){
            smallSp=left;
            bigSp=right;
        }else {
            smallSp=right;
            bigSp=left;
        }
        for(int beginPointer = 0; beginPointer< smallSp.begins.size()-1; beginPointer++) {
            int groupBegin = smallSp.begins.get(beginPointer);
            int groupEnd = smallSp.begins.get(beginPointer + 1);
            if (groupBegin != groupEnd - 1) {
                continue;
            }
            int lonelyTupleIndex=smallSp.indexes[groupBegin];
            if (bigSp.getGroupLengthConcerningIndex(lonelyTupleIndex)==1){
                tuplesToRemove.add(lonelyTupleIndex);
            }
        }
        if (tuplesToRemove.size()!=0){
            left.removeTuples(tuplesToRemove);
            right.removeTuples(tuplesToRemove);
        }
    }



    private long[] validateIncrementalSwapAndNeverViolate
            (SortedPartition parentSide,SortedPartition expandSide,SortedPartition otherSide){
        Statistics.addCount("g1增量计算次数");
        TimeStatistics.TimeStopper timer = TimeStatistics.start("g1精确计算");
        SortedPartition[] sps = removeNullWithParent(otherSide,expandSide,parentSide);
        long swapViolationCount=0,neverViolationCount=0;
        if(sps[0].begins.size() > 1 && sps[1].begins.size() > 1 && sps[2].begins.size() > 1) {
            int[] rightGroupIndex = sps[0].index2groupIndex;
            int[] leftGroupIndex = sps[1].index2groupIndex;

            int tupleCount = sps[1].index2groupIndex.length;
            int[] index2convertedRight;
            int[] groupLastRightGroup;
            int[] groupLastConvertedValue = new int[0];
            if (g1IncrementalUseStateCompaction) {
                index2convertedRight = new int[tupleCount];
                groupLastRightGroup = new int[sps[2].begins.size() - 1];
                groupLastConvertedValue = new int[sps[2].begins.size() - 1];
                Arrays.fill(groupLastRightGroup, -1);
                Arrays.fill(groupLastConvertedValue, -1);
                for (int i = 0; i < tupleCount; i++) {
                    int index = sps[0].indexes[i];
                    int leftGroup = sps[2].index2groupIndex[index];
                    int rightGroup = sps[0].index2groupIndex[index];
                    if (rightGroup > groupLastRightGroup[leftGroup]) {
                        groupLastConvertedValue[leftGroup]++;
                        groupLastRightGroup[leftGroup] = rightGroup;
                    }
                    index2convertedRight[index] = groupLastConvertedValue[leftGroup];
                }
            } else {
                index2convertedRight = sps[0].index2groupIndex;
            }

            for (int beginPointer = 0; beginPointer < sps[2].begins.size() - 1; beginPointer++) {
                int groupBegin = sps[2].begins.get(beginPointer);
                int groupEnd = sps[2].begins.get(beginPointer + 1);
                int expandedFirstGroup = sps[1].index2groupIndex[sps[1].indexes[groupBegin]];
                int expandedLastGroup = sps[1].index2groupIndex[sps[1].indexes[groupEnd - 1]];
                if (expandedFirstGroup == expandedLastGroup) {
                    continue;
                }
                int groupLength = groupEnd - groupBegin;
                if (groupLength >= segmentTreeThreshold) {
                    int rangeHigh;
                    if (g1IncrementalUseStateCompaction) {
                        rangeHigh = groupLastConvertedValue[beginPointer];
                    } else {
                        rangeHigh = sps[0].begins.size();
                    }
                    SegmentTreeForG1 tree = new SegmentTreeForG1(0, rangeHigh);
                    int[] int2Count = new int[rangeHigh + 1];
                    for (int subBeginPointer = expandedFirstGroup; subBeginPointer <= expandedLastGroup; subBeginPointer++) {
                        int subGroupBegin = sps[1].begins.get(subBeginPointer);
                        int subGroupEnd = sps[1].begins.get(subBeginPointer + 1);
                        for (int i = subGroupBegin; i < subGroupEnd; i++) {
                            int right = index2convertedRight[sps[1].indexes[i]];
                            int neverViolationIncrease = tree.query(0, right - 1);
                            neverViolationCount += neverViolationIncrease;
                            swapViolationCount += (subGroupBegin - groupBegin - int2Count[right] - neverViolationIncrease);
                        }
                        for (int i = subGroupBegin; i < subGroupEnd; i++) {
                            int right = index2convertedRight[sps[1].indexes[i]];
                            tree.insert(right);
                            int2Count[right]++;
                        }
                    }
                } else {
                    for (int i = groupBegin + 1; i < groupEnd; i++) {
                        int indexi = sps[1].indexes[i];
                        for (int j = groupBegin; j < i; j++) {
                            int indexj = sps[1].indexes[j];
                            if (leftGroupIndex[indexi] == leftGroupIndex[indexj]) {
                                break;
                            }
                            if (rightGroupIndex[indexi] > rightGroupIndex[indexj]) {
                                neverViolationCount++;
                            } else if (rightGroupIndex[indexi] < rightGroupIndex[indexj]) {
                                swapViolationCount++;
                            }
                        }
                    }
                }
            }
        }
        timer.stop();
        return new long[]{swapViolationCount,neverViolationCount};
    }

    private long[] validateFullSwapAndNeverViolate(){
        Statistics.addCount("g1完全计算次数");
        SortedPartition[] sps = removeNull(left,right);
        TimeStatistics.TimeStopper timer = TimeStatistics.start("g1精确计算");
        long swapViolationCount=0,neverViolationCount=0;
        if(sps[0].begins.size() > 1 && sps[1].begins.size() > 1) {
            int[] row2RightGroupIndex = sps[1].index2groupIndex;
            SegmentTreeForG1 tree = new SegmentTreeForG1(0, sps[1].begins.size() - 2);
            int[] globalInt2Count = new int[sps[1].begins.size() - 1];

            for (int beginPointer = 0; beginPointer < sps[0].begins.size() - 1; beginPointer++) {

                int groupBegin = sps[0].begins.get(beginPointer);
                int groupEnd = sps[0].begins.get(beginPointer + 1);

                for (int i = groupBegin; i < groupEnd; i++) {
                    int rightGroupIndex = row2RightGroupIndex[sps[0].indexes[i]];
                    int swapIncrease = tree.query(rightGroupIndex + 1, Integer.MAX_VALUE);
                    swapViolationCount += swapIncrease;
                    neverViolationCount += (groupBegin - swapIncrease - globalInt2Count[rightGroupIndex]);
                }

                for (int i = groupBegin; i < groupEnd; i++) {
                    int rightGroupIndex = row2RightGroupIndex[sps[0].indexes[i]];
                    tree.insert(rightGroupIndex);
                    globalInt2Count[rightGroupIndex]++;
                }
            }
        }
        timer.stop();
        return new long[]{swapViolationCount,neverViolationCount};
    }

    private long validateSplitViolation(){
        TimeStatistics.TimeStopper timer = TimeStatistics.start("g1精确计算");
        long splitViolationCount=0;
        SortedPartition[] sps = removeNull(left,right);
        if(sps[0].begins.size() > 1 && sps[1].begins.size() > 1) {
            int[] row2RightGroupIndex = sps[1].index2groupIndex;
            for (int beginPointer = 0; beginPointer < sps[0].begins.size() - 1; beginPointer++) {

                int groupBegin = sps[0].begins.get(beginPointer);
                int groupEnd = sps[0].begins.get(beginPointer + 1);
                int groupLength = groupEnd - groupBegin;
                HashMap<Integer, Integer> groupInt2count = new HashMap<>();

                for (int i = groupBegin; i < groupEnd; i++) {
                    int rightGroupIndex = row2RightGroupIndex[sps[0].indexes[i]];
                    groupInt2count.put(rightGroupIndex, groupInt2count.getOrDefault(rightGroupIndex, 0) + 1);
                }
                for (int count : groupInt2count.values()) {
                    splitViolationCount += (long) count * (groupLength - count);
                }
            }
            timer.stop();
        }
        return splitViolationCount;
    }

    private SortedPartition[] removeNull(SortedPartition left, SortedPartition right){
        SortedPartition[] partitions = new SortedPartition[2];
        ArrayList<Integer> removedIndex = new ArrayList<>();
        int nullCount = 0;


        for (int i = 0; i < left.nulls.length; i++){
            if(left.nulls[i] || right.nulls[i]) {
                removedIndex.add(i);
                nullCount++;
            }
        }
        System.out.println(dontUseNull);

        if(nullCount == 0 || dontUseNull)
            return new SortedPartition[]{left,right};

        int[] indexesL = new int[left.indexes.length-nullCount];
        int[] indexesR = new int[right.indexes.length-nullCount];

        int[] index2groupIndexL = new int[left.index2groupIndex.length-nullCount];
        int[] index2groupIndexR = new int[right.index2groupIndex.length-nullCount];

        //TODO implement actual functionality
        int index = 0;
        for(int i = 0; i < left.indexes.length;i++){
            if(!(left.nulls[i] || right.nulls[i])){
                indexesR[index] = right.indexes[i];
                indexesL[index] = left.indexes[i];
                index++;
            }
        }

        int[] subtractL = new int[indexesL.length];
        int[] subtractR = new int[indexesR.length];
        Arrays.fill(subtractL,0);
        Arrays.fill(subtractR,0);

        for(int i = 0; i < removedIndex.size();i++){
            for(int j = 0; j < indexesL.length; j++){
                if(left.indexes[removedIndex.get(i)] < indexesL[j])
                    subtractL[j] += 1;
            }
            for(int j = 0; j < indexesR.length; j++){
                if(right.indexes[removedIndex.get(i)] < indexesR[j])
                    subtractR[j] += 1;
            }
        }

        for (int i = 0; i < indexesL.length; i++){
            indexesL[i] -= subtractL[i];
        }
        for (int i = 0; i < indexesR.length; i++){
            indexesR[i] -= subtractR[i];
        }

        NewDawn newDawn = new NewDawn(left,right);

        partitions[0] = new SortedPartition(indexesL,newDawn.left,index2groupIndexL);
        partitions[1] = new SortedPartition(indexesR,newDawn.right,index2groupIndexR);
        partitions[0].updateIndex2IndexGroup();
        partitions[1].updateIndex2IndexGroup();

        boolean[] nullsL = new boolean [indexesL.length];
        boolean[] nullsR = new boolean[indexesR.length];

        Arrays.fill(nullsL,false);
        Arrays.fill(nullsR,false);

        partitions[0].nulls = nullsL;
        partitions[1].nulls = nullsR;
        return partitions;
    }


    private SortedPartition[] removeNullWithParent(SortedPartition left, SortedPartition right, SortedPartition other){
        SortedPartition[] partitions = new SortedPartition[3];
        ArrayList<Integer> removedIndex = new ArrayList<>();
        int nullCount = 0;

        for (int i = 0; i < left.nulls.length; i++){
            if(left.nulls[i] || right.nulls[i] || other.nulls[i]) {
                removedIndex.add(i);
                nullCount++;
            }
        }

        if(nullCount == 0)
            return new SortedPartition[]{left,right,other};


        int[] indexesL = new int[left.indexes.length-nullCount];
        int[] indexesR = new int[right.indexes.length-nullCount];
        int[] indexesP = new int[other.indexes.length-nullCount];

        int[] index2groupIndexL = new int[left.index2groupIndex.length-nullCount];
        int[] index2groupIndexR = new int[right.index2groupIndex.length-nullCount];
        int[] index2groupIndexP = new int[other.index2groupIndex.length-nullCount];

        //TODO implement actual functionality
        int index = 0;
        for(int i = 0; i < left.indexes.length;i++){
            if(!(left.nulls[i] || right.nulls[i] || other.nulls[i])){
                indexesR[index] = right.indexes[i];
                indexesL[index] = left.indexes[i];
                indexesP[index] = other.indexes[i];
                index++;
            }
        }

        int[] subtractL = new int[indexesL.length];
        int[] subtractR = new int[indexesR.length];
        int[] subtractP = new int[indexesP.length];
        Arrays.fill(subtractL,0);
        Arrays.fill(subtractR,0);
        Arrays.fill(subtractP,0);

        for(int i = 0; i < removedIndex.size();i++){
            for(int j = 0; j < indexesL.length; j++){
                if(left.indexes[removedIndex.get(i)] < indexesL[j])
                    subtractL[j] += 1;
            }
            for(int j = 0; j < indexesR.length; j++){
                if(right.indexes[removedIndex.get(i)] < indexesR[j])
                    subtractR[j] += 1;
            }
            for (int j = 0; j < indexesP.length;j++){
                if(other.indexes[removedIndex.get(i)] < indexesP[j])
                    subtractP[j] += 1;
            }
        }

        for (int i = 0; i < indexesL.length; i++){
            indexesL[i] -= subtractL[i];
        }
        for (int i = 0; i < indexesR.length; i++){
            indexesR[i] -= subtractR[i];
        }
        for (int i = 0; i < indexesP.length;i++){
            indexesP[i] -= subtractP[i];
        }

        NewDawn newDawn = new NewDawn(left,right,other);

        partitions[0] = new SortedPartition(indexesL,newDawn.left,index2groupIndexL);
        partitions[1] = new SortedPartition(indexesR,newDawn.right,index2groupIndexR);
        partitions[2] = new SortedPartition(indexesP,newDawn.other,index2groupIndexP);
        partitions[0].updateIndex2IndexGroup();
        partitions[1].updateIndex2IndexGroup();
        partitions[2].updateIndex2IndexGroup();

        boolean[] nullsL = new boolean [indexesL.length];
        boolean[] nullsR = new boolean[indexesR.length];
        boolean[] nullsP = new boolean[indexesR.length];

        Arrays.fill(nullsL,false);
        Arrays.fill(nullsR,false);
        Arrays.fill(nullsP,false);

        partitions[0].nulls = nullsL;
        partitions[1].nulls = nullsR;
        partitions[2].nulls = nullsP;
        return partitions;
    }

    public ValidationResultWithAccurateBound validateForALODWithG1() {
        if (g1ResultCache ==null){
            long splitViolationCount=validateSplitViolation();
            long[] swapAndNever;
            if (parent==null || !parent.hasResultCache()){
                swapAndNever=validateFullSwapAndNeverViolate();
            }else {
                if (right.equalsFast(parent.right)){
                    swapAndNever=validateIncrementalSwapAndNeverViolate(parent.left,left,right);
                }else {
                    swapAndNever=validateIncrementalSwapAndNeverViolate(parent.right,right,left);
                }
            }
            //按照最新的g1定义，swap算两个,上界也要相应调整
            swapAndNever[0]*=2;
            swapAndNever[1]*=2;

            long totalTuplePairCount =(long) left.getTupleCount() * (left.getTupleCount() -1);
            g1ResultCache = new ALODValidationResultWithBound(splitViolationCount, swapAndNever[0]
                    , swapAndNever[1], totalTuplePairCount, true);
            if (parent != null && parent.hasResultCache()) {
                g1ResultCache.errorRateLowerBound += parent.g1ResultCache.errorRateLowerBound;
                g1ResultCache.errorRate += parent.g1ResultCache.errorRateLowerBound;
                g1ResultCache.errorRateUpperBound -= 1 - parent.g1ResultCache.errorRateUpperBound;
            }
        }
        return g1ResultCache;
    }

    public ValidationResultWithAccurateBound validateForALODWithG3(){
        SortedPartition combineSP=left.deepClone();
        combineSP.intersect(right);

        TimeStatistics.TimeStopper timer = TimeStatistics.start("g3精确计算");
        SegmentTreeForG3 segmentTree=new SegmentTreeForG3(0,right.getGroupCount()-1);
        //misl = max increasing subsequence length
        int erMISL=0,ubMISL=0;
        int lbMISL=0;
        int lastIndexInLeft=-1;
        List<int[]> insertGroups=new ArrayList<>();
        for(int beginPointer = 0; beginPointer< combineSP.begins.size()-1; beginPointer++) {
            int groupBegin       = combineSP.begins.get(beginPointer);
            int groupEnd         = combineSP.begins.get(beginPointer + 1);
            int groupLength      = groupEnd - groupBegin;
            int beginTupleIndex  = combineSP.indexes[groupBegin];
            int indexInLeft      = left.index2groupIndex[beginTupleIndex];
            int indexInRight     = right.index2groupIndex[beginTupleIndex];

            if (indexInLeft != lastIndexInLeft){
                for (int[] insertGroup : insertGroups) {
                    segmentTree.insert(insertGroup[0],insertGroup[1],SegmentTreeForG3.Option.ERROR_RATE);
                    segmentTree.insert(insertGroup[0],insertGroup[2],SegmentTreeForG3.Option.LOWER_BOUND);
                }
                insertGroups.clear();
            }

            int erLength = groupLength + segmentTree.query(0,indexInRight, SegmentTreeForG3.Option.ERROR_RATE);
            erMISL=Math.max(erMISL,erLength);

            int lbLength = 1 + segmentTree.query(0,indexInRight-1, SegmentTreeForG3.Option.LOWER_BOUND);
            lbMISL=Math.max(lbLength,lbMISL);

            int ubLength = groupLength + segmentTree.query(0,indexInRight, SegmentTreeForG3.Option.UPPER_BOUND);
            ubMISL=Math.max(ubMISL,ubLength);
            segmentTree.insert(indexInRight,ubLength, SegmentTreeForG3.Option.UPPER_BOUND);

//            insertGroups.add(new int[]{indexInRight,erLength});
            insertGroups.add(new int[]{indexInRight,erLength,lbLength});
            lastIndexInLeft=indexInLeft;
        }
        double tupleCount=left.getTupleCount();
        double er=1-erMISL/tupleCount,erlb=1-ubMISL/tupleCount;
        double erub=1-lbMISL/tupleCount;
//        double erub=1;
        timer.stop();
        return new ValidationResultWithAccurateBound(er,true,erlb,erub);
    }

    public boolean equalToBeforeIntersect(){
        if (parent==null){
            return false;
        }
        if (expandLeft){
            return left.equalsFast(parent.left);
        }else {
            return right.equalsFast(parent.right);
        }
    }

    public boolean hasResultCache(){
        return g1ResultCache !=null;
    }
}
