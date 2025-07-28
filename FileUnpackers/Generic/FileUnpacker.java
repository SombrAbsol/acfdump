package FileUnpackers.Generic;

import hexUtils.HexInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public abstract class FileUnpacker {
   protected static HexInputStream his;

   public static int[] nextBytes(int length) throws IOException {
      return his.readBytes(length);
   }

   public static int nextChar() throws IOException {
      return his.readU8();
   }

   public static int nextWord() throws IOException {
      return his.readU16();
   }

   public static int nextlWord() throws IOException {
      return his.readlU16();
   }

   public static int nextDWord() throws IOException {
      return his.readS32();
   }

   public static int nextlDWord() throws IOException {
      return his.readlS32();
   }

   public static long nextQWord() throws IOException {
      return his.readS64();
   }

   public static long nextlQWord() throws IOException {
      return his.readlS64();
   }

   public static String reverse(String str) {
      StringBuffer sbuf = new StringBuffer();

      for(int i = 0; i < str.length(); ++i) {
         sbuf.append(str.charAt(str.length() - i - 1));
      }

      return sbuf.toString();
   }

   public static String da2str(int[] da) {
      StringBuffer sbuf = new StringBuffer();

      for(int i = 0; i < da.length; ++i) {
         sbuf.append((char)da[i]);
      }

      return sbuf.toString();
   }

   public static String da2trimstr(int[] da) {
      StringBuffer sbuf = new StringBuffer();

      for(int i = 0; i < da.length && da[i] > 0; ++i) {
         sbuf.append((char)da[i]);
      }

      return sbuf.toString();
   }

   public static String str2hex(String str) {
      StringBuffer sbuf = new StringBuffer();

      for(int i = 0; i < str.length(); ++i) {
         sbuf.append(dec2hex(str.charAt(i)));
      }

      return sbuf.toString();
   }

   public static int da2dec(int[] decarr, boolean isBigEndian) {
      return isBigEndian ? hex2dec(str2hex(reverse(da2str(decarr)))) : hex2dec(str2hex(da2str(decarr)));
   }

   public static int da2dec(int[] decarr) {
      return da2dec(decarr, true);
   }

   public static int hex2dec(String hex) {
      return (int)Long.parseLong(hex, 16);
   }

   public static String dec2hex(int dec) {
      String hex = Integer.toHexString(dec);
      return hex.length() == 1 ? "0" + hex : hex;
   }

   public static String fixLength(long dec, int length) {
      int nDigits = ("" + dec).length();
      String s = "";

      for(int i = 0; i < length - nDigits; ++i) {
         s = s + "0";
      }

      return s + dec;
   }

   public static void saveNext(long length, String fileoutpath) throws IOException {
      saveNext(length, fileoutpath, length, true);
   }

   public static void saveNext(long length, String fileoutpath, boolean overwrite) throws IOException {
      saveNext(length, fileoutpath, length, overwrite);
   }

   public static void saveNext(long length, String fileoutpath, long padTo) throws IOException {
      saveNext(length, fileoutpath, padTo, true);
   }

   public static void saveNext(long length, String fileoutpath, long padTo, boolean overwrite) throws IOException {
      if (!overwrite) {
         while((new File(fileoutpath)).exists()) {
            fileoutpath = fileoutpath.replace(".", "_.");
         }
      }

      DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File(fileoutpath)));

      long i;
      for(i = 0L; i < length; ++i) {
         dos.writeByte(his.read());
      }

      while(i < padTo) {
         dos.write(0);
         ++i;
      }

      dos.flush();
      dos.close();
   }

   public static void saveLast(String fileoutpath) throws IOException {
      DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File(fileoutpath)));

      int nextByte;
      while((nextByte = his.read()) != -1) {
         dos.write(nextByte);
      }

      dos.flush();
      dos.close();
      DataOutputStream var3 = null;
   }

   public static String tryGetExtension(int maxlength, String defaultExt) throws IOException {
      return tryGetExtension(maxlength, 2, defaultExt);
   }

   public static String tryGetExtension(int maxlength, int minlength, String defaultExt) throws IOException {
      if (his.peekU8() == 16) {
         return "lz10";
      } else if (his.peekU8() == 17) {
         return "lz11";
      } else if (his.peekU8() != 36 && his.peekU8() != 40) {
         if (his.peekU8() == 48) {
            return "rle";
         } else {
            his.savePosition();
            String ext = "";

            int next;
            for(int nRead = 0; nRead < maxlength; ext = ext + (char)next) {
               next = his.read();
               ++nRead;
               if ((next > 90 || next < 65) && (next > 122 || next < 97) && (next < 49 || next > 9) && next != 48) {
                  break;
               }
            }

            if (ext.length() <= minlength) {
               ext = defaultExt;
            }

            his.loadPosition();
            return ext;
         }
      } else {
         return "huff";
      }
   }

   public static String trimZeroes(String input) {
      while(input.endsWith("\u0000")) {
         input = input.substring(0, input.length() - 1);
      }

      return input;
   }

   public static HexInputStream loadHIS(String fname) throws IOException {
      if (his != null) {
         his.close();
      }

      return his = new HexInputStream(fname);
   }

   public static void goTo(long pos) throws IOException {
      his.goTo(pos);
   }

   public static void skip(long n) throws IOException {
      his.skip(n);
   }

   public static long currByte() {
      return his.getPosition();
   }
}
