package Lab5;

import java.util.Arrays;
import java.util.Scanner;
import java.util.LinkedList;
import java.util.stream.IntStream;

class ResizableArray<T>
{
    private T [] array;
    int size;
    int maxSize;

    public ResizableArray() {
        this.array = (T[]) new Object[20];
        size = 0;
        maxSize = 1;
    }

    public void addElement(T element) {
        if (size == maxSize)
        {
            T [] temp = array;
            array = (T[]) new Object[2*maxSize];
            IntStream.range(0,size).forEach(i->array[i] = temp[i]);
            maxSize*=2;
        }
        array[size++] = element;
    }

    public void avoidElement(int index)
    {
        int elementsToMove = size - 1 - index;
        if (elementsToMove>0)
        {
            System.arraycopy(array,index+1,array,index,elementsToMove);
        }
        array[--size] = null;
    }

    public boolean removeElement(T element) {
        if (element!=null)
        {
            for (int i=0;i<size;i++)
            {
                if (array[i].equals(element))
                {
                    avoidElement(i);
                    return true;
                }
            }
        }
        else {
            IntStream.range(0,size)
                    .filter(i->array[i] == null)
                    .anyMatch(i->{
                        avoidElement(i);
                        return true;
                    });
        }
        return false;
    }

    public boolean contains(T element) {
        return IntStream.range(0,size).anyMatch(i->array[i].equals(element));
    }
    public Object [] toArray()
    {
        return Arrays.copyOf(array,size);
    }
    public boolean isEmpty()
    {
        return size==0;
    }

    public int count() {
        return size;
    }
    public T elementAt(int idx)
    {
        if (idx<0 || idx>count())
        {
            throw new ArrayIndexOutOfBoundsException();
        }
        return array[idx];
    }
    public static <T> void copyAll(ResizableArray<? super T> dest, ResizableArray<? extends T> src)
    {
        int len = src.count();
        IntStream.range(0,len).forEach(i->dest.addElement(src.elementAt(i)));
    }

}
class IntegerArray extends ResizableArray<Integer>
{
    public IntegerArray() {
    }

    public double sum() {
        double sum = 0;
        for (int i=0;i<count();i++)
        {
            sum+=elementAt(i);
        }
        return sum;
    }

    public double mean() {
        return sum()/count();
    }

    public int countNonZero() {
        int count = 0;
        for (int i=0;i<count();i++)
        {
            if (elementAt(i)!=0)
            {
                count++;
            }
        }
        return count;
    }

    public IntegerArray distinct() {
        IntegerArray result = new IntegerArray();
        for (int i=0;i<count();i++)
        {
            boolean alreadyHaveIt = false;
            for (int j=0;j<result.count();j++)
            {
                if (this.elementAt(i) == result.elementAt(j))
                {
                    alreadyHaveIt = true;
                    break;
                }
            }
            if (!alreadyHaveIt)
            {
                result.addElement(this.elementAt(i));
            }
        }
        return result;

    }

    public IntegerArray increment(int offset) {
        IntegerArray result = new IntegerArray();
        for (int i=0;i<count();i++)
        {
            int current = elementAt(i).intValue();
            result.addElement(current + offset);
        }
        return result;
    }
}


public class ResizableArrayTest {

    public static void main(String[] args) {
        Scanner jin = new Scanner(System.in);
        int test = jin.nextInt();
        if ( test == 0 ) { //test ResizableArray on ints
            ResizableArray<Integer> a = new ResizableArray<Integer>();
            System.out.println(a.count());
            int first = jin.nextInt();
            a.addElement(first);
            System.out.println(a.count());
            int last = first;
            while ( jin.hasNextInt() ) {
                last = jin.nextInt();
                a.addElement(last);
            }
            System.out.println(a.count());
            System.out.println(a.contains(first));
            System.out.println(a.contains(last));
            System.out.println(a.removeElement(first));
            System.out.println(a.contains(first));
            System.out.println(a.count());
        }
        if ( test == 1 ) { //test ResizableArray on strings
            ResizableArray<String> a = new ResizableArray<String>();
            System.out.println(a.count());
            String first = jin.next();
            a.addElement(first);
            System.out.println(a.count());
            String last = first;
            for ( int i = 0 ; i < 4 ; ++i ) {
                last = jin.next();
                a.addElement(last);
            }
            System.out.println(a.count());
            System.out.println(a.contains(first));
            System.out.println(a.contains(last));
            System.out.println(a.removeElement(first));
            System.out.println(a.contains(first));
            System.out.println(a.count());
            ResizableArray<String> b = new ResizableArray<String>();
            ResizableArray.copyAll(b, a);
            System.out.println(b.count());
            System.out.println(a.count());
            System.out.println(a.contains(first));
            System.out.println(a.contains(last));
            System.out.println(b.contains(first));
            System.out.println(b.contains(last));
            ResizableArray.copyAll(b, a);
            System.out.println(b.count());
            System.out.println(a.count());
            System.out.println(a.contains(first));
            System.out.println(a.contains(last));
            System.out.println(b.contains(first));
            System.out.println(b.contains(last));
            System.out.println(b.removeElement(first));
            System.out.println(b.contains(first));
            System.out.println(b.removeElement(first));
            System.out.println(b.contains(first));

            System.out.println(a.removeElement(first));
            ResizableArray.copyAll(b, a);
            System.out.println(b.count());
            System.out.println(a.count());
            System.out.println(a.contains(first));
            System.out.println(a.contains(last));
            System.out.println(b.contains(first));
            System.out.println(b.contains(last));
        }
        if ( test == 2 ) { //test IntegerArray
            IntegerArray a = new IntegerArray();
            System.out.println(a.isEmpty());
            while ( jin.hasNextInt() ) {
                a.addElement(jin.nextInt());
            }
            jin.next();
            System.out.println(a.sum());
            System.out.println(a.mean());
            System.out.println(a.countNonZero());
            System.out.println(a.count());
            IntegerArray b = a.distinct();
            System.out.println(b.sum());
            IntegerArray c = a.increment(5);
            System.out.println(c.sum());
            if ( a.sum() > 100 )
                ResizableArray.copyAll(a, a);
            else
                ResizableArray.copyAll(a, b);
            System.out.println(a.sum());
            System.out.println(a.removeElement(jin.nextInt()));
            System.out.println(a.sum());
            System.out.println(a.removeElement(jin.nextInt()));
            System.out.println(a.sum());
            System.out.println(a.removeElement(jin.nextInt()));
            System.out.println(a.sum());
            System.out.println(a.contains(jin.nextInt()));
            System.out.println(a.contains(jin.nextInt()));
        }
        if ( test == 3 ) { //test insanely large arrays
            LinkedList<ResizableArray<Integer>> resizable_arrays = new LinkedList<ResizableArray<Integer>>();
            for ( int w = 0 ; w < 500 ; ++w ) {
                ResizableArray<Integer> a = new ResizableArray<Integer>();
                int k =  2000;
                int t =  1000;
                for ( int i = 0 ; i < k ; ++i ) {
                    a.addElement(i);
                }

                a.removeElement(0);
                for ( int i = 0 ; i < t ; ++i ) {
                    a.removeElement(k-i-1);
                }
                resizable_arrays.add(a);
            }
            System.out.println("You implementation finished in less then 3 seconds, well done!");
        }
    }

}
