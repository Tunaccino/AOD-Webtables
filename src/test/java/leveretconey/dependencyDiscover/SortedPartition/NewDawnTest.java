package leveretconey.dependencyDiscover.SortedPartition;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class NewDawnTest {

    @Test
    void beginsUpperBigger() {
        NewDawn dawn = new NewDawn();
        ArrayList<Integer> left = new ArrayList<>(Arrays.asList(0,2,5));
        ArrayList<Integer> right = new ArrayList<>(Arrays.asList(0,1,5));
        ArrayList<ArrayList<Integer>> begins = dawn.beginsUpper(3,left,right);
        Assertions.assertEquals(begins.get(0),new ArrayList<>(Arrays.asList(0,2)));
        Assertions.assertEquals(begins.get(1),new ArrayList<>(Arrays.asList(0,1,2)));
    }

    @Test
    void beginsUpperEqual() {
        NewDawn dawn = new NewDawn();
        ArrayList<Integer> left = new ArrayList<>(Arrays.asList(0,2,5));
        ArrayList<Integer> right = new ArrayList<>(Arrays.asList(0,2,5));
        ArrayList<ArrayList<Integer>> begins = dawn.beginsUpper(3,left,right);
        Assertions.assertEquals(begins.get(0),new ArrayList<>(Arrays.asList(0,2)));
        Assertions.assertEquals(begins.get(1),new ArrayList<>(Arrays.asList(0,2)));
    }

    @Test
    void beginsUpperSmallerEqual() {
        NewDawn dawn = new NewDawn();
        ArrayList<Integer> left = new ArrayList<>(Arrays.asList(0,2,5));
        ArrayList<Integer> right = new ArrayList<>(Arrays.asList(0,2,3,5));
        ArrayList<ArrayList<Integer>> begins = dawn.beginsUpper(3,left,right);
        Assertions.assertEquals(begins.get(0),new ArrayList<>(Arrays.asList(0,2)));
        Assertions.assertEquals(begins.get(1),new ArrayList<>(Arrays.asList(0,2)));

    }

    @Test
    void beginsUpperSmallerBigger() {
        NewDawn dawn = new NewDawn();
        ArrayList<Integer> left = new ArrayList<>(Arrays.asList(0,2,5));
        ArrayList<Integer> right = new ArrayList<>(Arrays.asList(0,1,4,5));
        ArrayList<ArrayList<Integer>> begins = dawn.beginsUpper(3,left,right);
        Assertions.assertEquals(begins.get(0),new ArrayList<>(Arrays.asList(0,2)));
        Assertions.assertEquals(begins.get(1),new ArrayList<>(Arrays.asList(0,1,2)));

    }

    @Test
    void beginsLowerBigger() {
        NewDawn dawn = new NewDawn();
        ArrayList<Integer> left = new ArrayList<>(Arrays.asList(0,3,4,5));
        ArrayList<Integer> right = new ArrayList<>(Arrays.asList(0,4,5));
        ArrayList<ArrayList<Integer>> begins = dawn.beginsLower(3,left,right);
        Assertions.assertEquals(begins.get(0),new ArrayList<>(Arrays.asList(0,1,2)));
        Assertions.assertEquals(begins.get(1),new ArrayList<>(Arrays.asList(0,1,2)));
    }

    @Test
    void beginsLowerEqual() {
        NewDawn dawn = new NewDawn();
        ArrayList<Integer> left = new ArrayList<>(Arrays.asList(0,3,4,5));
        ArrayList<Integer> right = new ArrayList<>(Arrays.asList(0,3,5));
        ArrayList<ArrayList<Integer>> begins = dawn.beginsLower(3,left,right);
        Assertions.assertEquals(begins.get(0),new ArrayList<>(Arrays.asList(0,1,2)));
        Assertions.assertEquals(begins.get(1),new ArrayList<>(Arrays.asList(0,2)));
    }

    @Test
    void beginsLowerSmallerEqual() {
        NewDawn dawn = new NewDawn();
        ArrayList<Integer> left = new ArrayList<>(Arrays.asList(0,3,4,5));
        ArrayList<Integer> right = new ArrayList<>(Arrays.asList(0,1,3,5));
        ArrayList<ArrayList<Integer>> begins = dawn.beginsLower(3,left,right);
        Assertions.assertEquals(begins.get(0),new ArrayList<>(Arrays.asList(0,1,2)));
        Assertions.assertEquals(begins.get(1),new ArrayList<>(Arrays.asList(0,2)));
    }

    @Test
    void beginsLowerSmallerBigger() {
        NewDawn dawn = new NewDawn();
        ArrayList<Integer> left = new ArrayList<>(Arrays.asList(0,3,4,5));
        ArrayList<Integer> right = new ArrayList<>(Arrays.asList(0,1,4,5));
        ArrayList<ArrayList<Integer>> begins = dawn.beginsLower(3,left,right);
        Assertions.assertEquals(begins.get(0),new ArrayList<>(Arrays.asList(0,1,2)));
        Assertions.assertEquals(begins.get(1),new ArrayList<>(Arrays.asList(0,1,2)));
    }

    @Test
    void beginsLowerUpperLowerUpper() {
        NewDawn dawn = new NewDawn();
        ArrayList<Integer> left = new ArrayList<>(Arrays.asList(0,3,4,5,7,10));
        ArrayList<Integer> right = new ArrayList<>(Arrays.asList(0,1,4,5,10));
        ArrayList<ArrayList<Integer>> begins = dawn.beginsLower(3,left,right);
        ArrayList<ArrayList<Integer>> newBegins = dawn.beginsUpper(3,begins.get(0),begins.get(1));
        Assertions.assertEquals(newBegins.get(0),new ArrayList<>(Arrays.asList(0,1,2,4)));
        Assertions.assertEquals(newBegins.get(1),new ArrayList<>(Arrays.asList(0,1,2,4)));
    }

    @Test
    void beginsThree() {
        NewDawn dawn = new NewDawn();
        ArrayList<Integer> left = new ArrayList<>(Arrays.asList(0,3,4,5,7,10));
        ArrayList<Integer> right = new ArrayList<>(Arrays.asList(0,1,4,5,10));
        ArrayList<Integer> other = new ArrayList<>(Arrays.asList(0,4,6,8,10));
        ArrayList<ArrayList<Integer>> begins = dawn.beginsLower(3,left,right,other);
        ArrayList<ArrayList<Integer>> newBegins = dawn.beginsUpper(3,begins.get(0),begins.get(1),begins.get(2));
        Assertions.assertEquals(newBegins.get(0),new ArrayList<>(Arrays.asList(0,1,2,4)));
        Assertions.assertEquals(newBegins.get(1),new ArrayList<>(Arrays.asList(0,1,2,4)));
        Assertions.assertEquals(newBegins.get(2),new ArrayList<>(Arrays.asList(0,1,3,4)));
    }
}