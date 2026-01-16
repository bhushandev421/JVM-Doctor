package com.jvm.doctor.parsers;

import com.jvm.doctor.model.HeapInfo;
import com.jvm.doctor.model.VmFlags;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ParserTest {

    @Test
    public void testVmFlagsParser() {
        String input = "-XX:CICompilerCount=4 -XX:ConcGCThreads=2 -XX:G1ConcRefinementThreads=8 -XX:G1HeapRegionSize=1048576 -XX:GCDrainStackTargetSize=64 -XX:InitialHeapSize=268435456 -XX:MarkStackSize=4194304 -XX:MaxHeapSize=4294967296 -XX:MaxNewSize=2576351232 -XX:MinHeapDeltaBytes=1048576 -XX:NonNMethodCodeHeapSize=5836300 -XX:NonProfiledCodeHeapSize=122911450 -XX:ProfiledCodeHeapSize=122911450 -XX:ReservedCodeCacheSize=251658240 -XX:+SegmentedCodeCache -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseFastUnorderedTimeStamps -XX:+UseG1GC -XX:-UseParallelGC";

        VmFlagsParser parser = new VmFlagsParser();
        VmFlags flags = parser.parse(input);

        Assertions.assertTrue(flags.isUseG1GC());
        Assertions.assertFalse(flags.isUseParallelGC());
        Assertions.assertEquals(4294967296L, flags.getXmx());
        Assertions.assertEquals(268435456L, flags.getXms());
    }

    @Test
    public void testHeapInfoParserG1() {
        String input = "garbage-first heap   total 514048K, used 123456K [0x0000000700000000, 0x0000000800000000, 0x0000000800000000)\n"
                +
                "  region size 2048K, 12 young (24576K), 0 survivors (0K)\n" +
                " Metaspace       used 15302K, capacity 15550K, committed 15616K, reserved 1062912K\n" +
                "  class space    used 1791K, capacity 1965K, committed 2048K, reserved 1048576K";

        HeapInfoParser parser = new HeapInfoParser();
        HeapInfo info = parser.parse(input);

        Assertions.assertEquals(514048 * 1024L, info.getHeapMaxBytes());
        Assertions.assertEquals(123456 * 1024L, info.getHeapUsedBytes());
        Assertions.assertEquals(15302 * 1024L, info.getMetaspaceUsedBytes());
    }
}
