package leveretconey.dependencyDiscover.SortedPartition;

import java.util.ArrayList;

public class NewDawn {

    public NewDawn() {
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
