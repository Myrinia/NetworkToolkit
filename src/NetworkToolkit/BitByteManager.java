package NetworkToolkit;

public class BitByteManager {
	
	public static String humanReadableByteCountSI(long bytes) {
	    String s = bytes < 0 ? "-" : "";
	    long b = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
	    
	    return  b < 1000L ? bytes + " B"
	            : b < 999_950L ? String.format("%s%.1f kB", s, b / 1e3)
	            : (b /= 1000) < 999_950L ? String.format("%s%.1f MB", s, b / 1e3)
	            : (b /= 1000) < 999_950L ? String.format("%s%.1f GB", s, b / 1e3)
	            : (b /= 1000) < 999_950L ? String.format("%s%.1f TB", s, b / 1e3)
	            : (b /= 1000) < 999_950L ? String.format("%s%.1f PB", s, b / 1e3)
	            : String.format("%s%.1f EB", s, b / 1e6);
	}
	
	public static String humanReadableByteCountSI(long bytes, boolean asInPerSecond) {
		if(asInPerSecond) {
			return humanReadableByteCountSI(bytes) + "/s";
		}else {
			return humanReadableByteCountSI(bytes);
		}
	}
	
	public static String humanReadableBitCountSI(long bytes, boolean asInPerSecond) {
		bytes *= 8;
		if(asInPerSecond) {
			return humanReadableByteCountSI(bytes) + "/s";
		}else {
			return humanReadableByteCountSI(bytes);
		}
	}
	
	public static String humanReadableBitCountSI(long bytes) {
		return humanReadableBitCountSI(bytes,false);
	}
	
	public static String humanReadableByteCountBin(long bytes) {
	    long b = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
	    return b < 1024L ? bytes + " B"
	            : b <= 0xfffccccccccccccL >> 40 ? String.format("%.1f KiB", bytes / 0x1p10)
	            : b <= 0xfffccccccccccccL >> 30 ? String.format("%.1f MiB", bytes / 0x1p20)
	            : b <= 0xfffccccccccccccL >> 20 ? String.format("%.1f GiB", bytes / 0x1p30)
	            : b <= 0xfffccccccccccccL >> 10 ? String.format("%.1f TiB", bytes / 0x1p40)
	            : b <= 0xfffccccccccccccL ? String.format("%.1f PiB", (bytes >> 10) / 0x1p40)
	            : String.format("%.1f EiB", (bytes >> 20) / 0x1p40);

	}
	
	public static String humanReadableByteCountBin(long bytes, boolean asInPerSecond) {
		if(asInPerSecond) {
			return humanReadableByteCountBin(bytes) + "/s";
		}else {
			return humanReadableByteCountBin(bytes);
		}
	}
	
	public static String humanReadableBitCountBin(long bytes, boolean asInPerSecond) {
		bytes *= 8;
		if(asInPerSecond) {
			return humanReadableByteCountSI(bytes) + "/s";
		}else {
			return humanReadableByteCountSI(bytes);
		}
	}
	
	public static String humanReadableBitCountBin(long bytes) {
		return humanReadableBitCountBin(bytes,false);
	}
	
}
