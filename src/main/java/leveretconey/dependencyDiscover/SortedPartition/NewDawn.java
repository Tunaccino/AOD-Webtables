package leveretconey.dependencyDiscover.SortedPartition;

import java.util.ArrayList;

public class NewDawn {
    public ArrayList<Integer> left;
    public ArrayList<Integer> right;
    public ArrayList<Integer> other;

    private int leftLower,leftUpper,rightLower,rightUpper,otherLower,otherUpper = 0;


    public NewDawn(SortedPartition ... partitions) {
        this.left = new ArrayList<>(partitions[0].begins);
        this.right = new ArrayList<>(partitions[1].begins);
        boolean three = partitions.length > 2;
        if(three)
            this.other = new ArrayList<>(partitions[2].begins);

        int leftSize = partitions[0].nulls.length-1;
        int rightSize = partitions[1].nulls.length-1;
        int otherSize = 0;

        if(three)
            otherSize = partitions[2].nulls.length-1;



        while (leftUpper <= leftSize && partitions[0].nulls[leftSize-leftUpper])
            leftUpper++;

        while (rightUpper <= rightSize && partitions[1].nulls[rightSize - rightUpper])
            rightUpper++;

        while (rightLower < partitions[1].nulls.length && partitions[1].nulls[rightLower])
            rightLower++;

        while (leftLower < partitions[0].nulls.length && partitions[0].nulls[leftLower])
            leftLower++;

        if(three){
            while (otherUpper <= otherSize && partitions[2].nulls[otherSize - otherUpper])
                otherUpper++;

            while (otherLower < partitions[2].nulls.length && partitions[2].nulls[otherLower])
                otherLower++;

        }

        if(partitions[0].nulls[0] || partitions[1].nulls[0] || (three && partitions[2].nulls[0])){
            if(leftLower == Integer.max(leftLower,(Integer.max(rightLower,otherLower)))){
                if(three)  beginsLower(leftLower,left,right,other); else beginsLower(leftLower,left,right);
            }
            else if(rightLower == Integer.max(leftLower,(Integer.max(rightLower,otherLower)))){
                if(three) beginsLower(rightLower,right,left,other); else beginsLower(rightLower,right,left);
            }
            else {
                beginsLower(otherLower,other,left,right);
            }
        }
        if(partitions[0].nulls[leftSize] || partitions[1].nulls[rightSize] || (three && partitions[2].nulls[otherSize])){
            if(leftUpper == Integer.max(leftUpper,(Integer.max(rightUpper,otherUpper)))){
                if(three) beginsUpper(leftUpper,left,right,other); else beginsUpper(leftUpper,left,right);
            }
            else if(rightUpper == Integer.max(leftUpper,(Integer.max(rightUpper,otherUpper)))){
                if(three) beginsUpper(rightUpper,right,left,other); else beginsUpper(rightUpper,right,left);
            }
            else {
                beginsUpper(otherUpper,other,left,right);
            }

        }

    }


    public ArrayList<ArrayList<Integer>> beginsUpper(int upperCount, ArrayList<Integer>... oldBegins) {
        ArrayList<Integer> left = oldBegins[0];
        ArrayList<Integer> right = oldBegins[1];
        ArrayList<ArrayList<Integer>> begins = new ArrayList<>();
        left.remove(left.size() - 1);
        int lastIndex = right.size() - 1;
        if ((right.get(lastIndex) - right.get(lastIndex - 1)) > upperCount)
            beginsUpperBigger(upperCount, right);

        else if ((right.get(lastIndex) - right.get(lastIndex - 1)) == upperCount)
            beginsUpperEqual(upperCount, right);

        else
            beginsUpperSmaller(upperCount, right);

        begins.add(left);
        begins.add(right);

        if (oldBegins.length > 2) {
            ArrayList<Integer> other = oldBegins[2];
            lastIndex = other.size() - 1;
            if ((other.get(lastIndex) - other.get(lastIndex - 1)) > upperCount)
                beginsUpperBigger(upperCount, other);

            else if ((other.get(lastIndex) - other.get(lastIndex - 1)) == upperCount)
                beginsUpperEqual(upperCount, other);

            else
                beginsUpperSmaller(upperCount, other);

            begins.add(other);
        }
        return begins;
    }


    public ArrayList<ArrayList<Integer>> beginsLower(int lowerCount, ArrayList<Integer>... oldBegins) {
        ArrayList<Integer> left = oldBegins[0];
        ArrayList<Integer> right = oldBegins[1];
        ArrayList<ArrayList<Integer>> begins = new ArrayList<>();

        left.remove(1);
        for (int i = 1; i < left.size(); i++)
            left.set(i, left.get(i) - lowerCount);

        if (right.get(1) > lowerCount)
            beginsLowerBigger(lowerCount, right);

        else if (right.get(1) == lowerCount)
            beginsLowerEqual(lowerCount, right);
        else
            beginsLowerSmaller(lowerCount, right);

        begins.add(left);
        begins.add(right);

        if (oldBegins.length > 2) {
            ArrayList<Integer> other = oldBegins[2];
            if (other.get(1) > lowerCount)
                beginsLowerBigger(lowerCount, other);

            else if (other.get(1) == lowerCount)
                beginsLowerEqual(lowerCount, other);
            else
                beginsLowerSmaller(lowerCount, other);

            begins.add(other);
        }
        return begins;
    }

    private void beginsUpperBigger(int upperCount, ArrayList<Integer> list) {
        int lastIndex = list.size() - 1;
        list.set(lastIndex, list.get(lastIndex) - upperCount);
    }

    private void beginsUpperEqual(int upperCount, ArrayList<Integer> list) {
        int lastIndex = list.size() - 1;
        list.remove(lastIndex);
    }

    private void beginsUpperSmaller(int upperCount, ArrayList<Integer> list) {
        int lastIndex = list.size() - 1;
        int i = 2;

        while (list.get(lastIndex) - list.get(lastIndex - i) < upperCount)
            i++;


        if ((list.get(lastIndex) - list.get(lastIndex - i)) == upperCount) {
            for (; i > 0; i--)
                list.removeLast();

        } else {
            int diff = upperCount - (list.get(lastIndex) - list.get(lastIndex - (i - 1)));

            for (; i > 1; i--)
                list.removeLast();

            list.set(list.size() - 1, list.getLast() - diff);

        }
    }

    private void beginsLowerBigger(int lowerCount, ArrayList<Integer> list) {
        for (int i = 1; i < list.size(); i++)
            list.set(i, list.get(i) - lowerCount);
    }

    private void beginsLowerEqual(int lowerCount, ArrayList<Integer> list) {
        list.remove(1);
        for (int i = 1; i < list.size(); i++)
            list.set(i, list.get(i) - lowerCount);
    }

    private void beginsLowerSmaller(int lowerCount, ArrayList<Integer> list) {
        int i = 2;
        while (list.get(i) < lowerCount)
            i++;

        if (list.get(i) == lowerCount) {
            for (; i > 0; i--)
                list.remove(1);

            for (int j = 1; j < list.size(); j++) {
                list.set(j, list.get(j) - lowerCount);
            }

        } else {
            for (; i > 1; i--)
                list.remove(1);

            for (int j = 1; j < list.size(); j++)
                list.set(j, list.get(j) - lowerCount);

        }
    }
}
