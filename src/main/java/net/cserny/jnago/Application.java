package net.cserny.jnago;

import com.sun.jna.*;

import java.util.Arrays;
import java.util.List;

public class Application
{
    public interface Awesome extends Library
    {
        class GoSlice extends Structure
        {
            static class ByValue extends GoSlice implements Structure.ByValue {}

            public Pointer data;
            public long len;
            public long cap;

            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("data", "len", "cap");
            }
        }

        class GoString extends Structure
        {
            static class ByValue extends GoString implements Structure.ByValue {}

            public String p;
            public long n;

            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("p", "n");
            }
        }

        long Add(long a, long b);
        double Cosine(double val);
        void Sort(GoSlice.ByValue values);
        double Log(GoString.ByValue string);
    }

    public static void main(String[] args) {
        Awesome awesome = Native.loadLibrary("./awesome.so", Awesome.class);

        System.out.printf("awesome.Add(12, 99) = %s\n", awesome.Add(12, 99));
        System.out.printf("awesome.Cosine(1.0) = %s\n", awesome.Cosine(1.0));

        long[] nums = new long[] {53, 11, 5, 2, 88};
        Memory arr = new Memory(nums.length * Native.getNativeSize(Long.TYPE));
        arr.write(0, nums, 0, nums.length);

        Awesome.GoSlice.ByValue slice = new Awesome.GoSlice.ByValue();
        slice.data = arr; // Pointer!
        slice.len = nums.length;
        slice.cap = nums.length;
        awesome.Sort(slice);
        System.out.print("awesome.Sort(53,11,5,2,88) = [");
        long[] sorted = slice.data.getLongArray(0, nums.length);
        for (long aSorted : sorted) {
            System.out.print(aSorted + " ");
        }
        System.out.println("]");

        Awesome.GoString.ByValue string = new Awesome.GoString.ByValue();
        string.p = "Hello Java";
        string.n = string.p.length();
        System.out.printf("msgid %f%n", awesome.Log(string));
    }
}
