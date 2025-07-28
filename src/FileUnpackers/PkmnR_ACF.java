package FileUnpackers;

import FileUnpackers.Generic.FileUnpacker;
import hexUtils.ArrayInputStream;
import hexUtils.HexInputStream;
import hexUtils.InvalidFileException;
import hexUtils.JavaDSDecmp;
import hexUtils.Triplet;
import java.io.File;
import java.util.ArrayList;

public class PkmnR_ACF extends FileUnpacker {
   public static void main(String[] args) {
      for(String arg : args) {
         File f = new File(arg);
         if (f.isDirectory()) {
            unpackFolder(arg);
         } else if (f.isFile()) {
            try {
               unpack(arg);
            } catch (Exception e) {
               e.printStackTrace();
            }
         }
      }

      System.out.println("Done.");
   }

   public static void unpackFolder(String flrName) {
      if (!flrName.endsWith("/")) {
         flrName = flrName + "/";
      }

      String[] var4;
      for(String fname : var4 = (new File(flrName)).list()) {
         if (fname.endsWith(".acf")) {
            try {
               unpack(flrName + fname);
            } catch (Exception e) {
               e.printStackTrace();
            }
         }
      }

   }

   public static void unpack(String fname) throws Exception {
      loadHIS(fname);
      if (!his.readString(4).equals("acf\u0000")) {
         throw new InvalidFileException(fname + " has no magic header 'acf\u0000'");
      } else if (nextDWord() != 32) {
         throw new InvalidFileException(fname + ": header size is not 0x20");
      } else {
         int dataStart = nextDWord();
         int numel = nextDWord();
         System.out.println("Total files inside " + fname + ": " + numel);
         if (nextDWord() != 1) {
            throw new InvalidFileException(fname + ": 5th DWORD not 1");
         } else if (nextDWord() != 50) {
            throw new InvalidFileException(fname + ": 6th DWORD not 0x32");
         } else if (nextDWord() != 0) {
            throw new InvalidFileException(fname + ": 7th DWORD not padding");
         } else if (nextDWord() != 0) {
            throw new InvalidFileException(fname + ": 8th DWORD not padding");
         } else {
            ArrayList<Triplet<Integer, Integer, Integer>> data = new ArrayList<>();

            for(int i = 0; i < numel; ++i) {
               Triplet<Integer, Integer, Integer> newelem = new Triplet<>(nextDWord(), nextDWord(), nextDWord());
               data.add(newelem);
            }

            String outdir = fname.substring(0, fname.lastIndexOf(".")) + "/";
            (new File(outdir)).mkdirs();

            for(int i = 0; i < numel; ++i) {
               Triplet<Integer, Integer, Integer> elem = (Triplet<Integer, Integer, Integer>)data.get(i);
               if ((Integer)elem.getFirst() == -1) {
                  if ((Integer)elem.getSecond() != 0 || (Integer)elem.getThird() != 0) {
                     throw new Exception(fname + ": dummy pointer, but not dummy sizes");
                  }
               } else {
                  goTo((long)((Integer)elem.getFirst() + dataStart));
                  String ext = "." + tryGetExtension(4, 2, "dat");
                  String outfname = outdir + fixLength((long)i, ("" + numel).length()) + ext;
                  if ((Integer)elem.getThird() == 0) {
                     saveNext((long)(Integer)elem.getSecond(), outfname);
                  } else {
                     int[] uncomp = JavaDSDecmp.Decompress(his);
                     ArrayInputStream ais = new ArrayInputStream(uncomp);
                     HexInputStream realHIS = his;
                     his = new HexInputStream(ais);
                     ext = "." + tryGetExtension(4, 2, "dat");
                     outfname = outdir + fixLength((long)i, ("" + numel).length()) + ext;
                     saveNext((long)uncomp.length, outfname, (long)(Integer)elem.getSecond());
                     his = realHIS;
                  }

                  if ((i + 1) % 25 == 0) {
                     System.out.println("Unpacked " + (i + 1) + " of " + numel);
                  }
               }
            }

         }
      }
   }
}
