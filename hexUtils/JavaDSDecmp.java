package hexUtils;

import java.io.EOFException;
import java.io.IOException;

public class JavaDSDecmp {
   public static int[] Decompress(HexInputStream his) throws IOException {
      switch (his.readU8()) {
         case 16:
            return Decompress10LZ(his);
         case 17:
            return Decompress11LZ(his);
         case 36:
         case 40:
            return DecompressHuff(his);
         case 48:
            return DecompressRLE(his);
         default:
            return null;
      }
   }

   private static int getLength(HexInputStream his) throws IOException {
      int length = 0;

      for(int i = 0; i < 3; ++i) {
         length |= his.readU8() << i * 8;
      }

      if (length == 0) {
         length = his.readlS32();
      }

      return length;
   }

   private static int[] Decompress10LZ(HexInputStream his) throws IOException {
      int[] outData = new int[getLength(his)];
      int curr_size = 0;

      while(curr_size < outData.length) {
         int flags;
         try {
            flags = his.readU8();
         } catch (EOFException var15) {
            break;
         }

         for(int i = 0; i < 8 && curr_size < outData.length; ++i) {
            boolean flag = (flags & 128 >> i) > 0;
            if (!flag) {
               int b;
               try {
                  b = his.readU8();
               } catch (EOFException var14) {
                  break;
               }

               try {
                  outData[curr_size++] = b;
               } catch (ArrayIndexOutOfBoundsException var13) {
                  if (b == 0) {
                     break;
                  }
               }

               if (curr_size >= outData.length) {
                  break;
               }
            } else {
               int disp = 0;

               int b;
               try {
                  b = his.readU8();
               } catch (EOFException var12) {
                  throw new InvalidFileException("Incomplete data");
               }

               int n = b >> 4;
               disp = (b & 15) << 8;

               try {
                  disp |= his.readU8();
               } catch (EOFException var11) {
                  throw new InvalidFileException("Incomplete data");
               }

               n += 3;
               int cdest = curr_size;
               if (disp > curr_size) {
                  throw new InvalidFileException("Cannot go back more than already written");
               }

               for(int j = 0; j < n && curr_size < outData.length; ++j) {
                  outData[curr_size++] = outData[cdest - disp - 1 + j];
               }

               if (curr_size >= outData.length) {
                  break;
               }
            }
         }
      }

      return outData;
   }

   private static int[] Decompress11LZ(HexInputStream his) throws IOException {
      int[] outData = new int[getLength(his)];
      int curr_size = 0;

      while(curr_size < outData.length) {
         int flags;
         try {
            flags = his.readU8();
         } catch (EOFException var20) {
            break;
         }

         for(int i = 0; i < 8 && curr_size < outData.length; ++i) {
            boolean flag = (flags & 128 >> i) > 0;
            if (!flag) {
               try {
                  outData[curr_size++] = his.readU8();
               } catch (EOFException var19) {
                  break;
               }

               if (curr_size > outData.length) {
                  break;
               }
            } else {
               int b1;
               try {
                  b1 = his.readU8();
               } catch (EOFException var18) {
                  throw new InvalidFileException("Incomplete data");
               }

               int len;
               int disp;
               switch (b1 >> 4) {
                  case 0:
                     len = b1 << 4;

                     int bt;
                     try {
                        bt = his.readU8();
                     } catch (EOFException var17) {
                        throw new InvalidFileException("Incomplete data");
                     }

                     len |= bt >> 4;
                     len += 17;
                     disp = (bt & 15) << 8;

                     int b2;
                     try {
                        b2 = his.readU8();
                     } catch (EOFException var16) {
                        throw new InvalidFileException("Incomplete data");
                     }

                     disp |= b2;
                     break;
                  case 1:
                     int bt;
                     int b2;
                     int b3;
                     try {
                        bt = his.readU8();
                        b2 = his.readU8();
                        b3 = his.readU8();
                     } catch (EOFException var15) {
                        throw new InvalidFileException("Incomplete data");
                     }

                     len = (b1 & 15) << 12;
                     len |= bt << 4;
                     len |= b2 >> 4;
                     len += 273;
                     disp = (b2 & 15) << 8;
                     disp |= b3;
                     break;
                  default:
                     len = (b1 >> 4) + 1;
                     disp = (b1 & 15) << 8;

                     int b2;
                     try {
                        b2 = his.readU8();
                     } catch (EOFException var14) {
                        throw new InvalidFileException("Incomplete data");
                     }

                     disp |= b2;
               }

               if (disp > curr_size) {
                  throw new InvalidFileException("Cannot go back more than already written");
               }

               int cdest = curr_size;

               for(int j = 0; j < len && curr_size < outData.length; ++j) {
                  outData[curr_size++] = outData[cdest - disp - 1 + j];
               }

               if (curr_size > outData.length) {
                  break;
               }
            }
         }
      }

      return outData;
   }

   private static int[] DecompressRLE(HexInputStream his) throws IOException {
      int[] outData = new int[getLength(his)];
      int curr_size = 0;

      do {
         int flag;
         try {
            flag = his.readU8();
         } catch (EOFException var10) {
            break;
         }

         boolean compressed = (flag & 128) > 0;
         int rl = flag & 127;
         if (compressed) {
            rl += 3;
         } else {
            ++rl;
         }

         if (compressed) {
            int b;
            try {
               b = his.readU8();
            } catch (EOFException var9) {
               break;
            }

            for(int i = 0; i < rl; ++i) {
               outData[curr_size++] = b;
            }
         } else {
            for(int i = 0; i < rl; ++i) {
               try {
                  outData[curr_size++] = his.readU8();
               } catch (EOFException var11) {
                  break;
               }
            }
         }

         if (curr_size > outData.length) {
            throw new InvalidFileException("curr_size > decomp_size; " + curr_size + ">" + outData.length);
         }
      } while(curr_size != outData.length);

      return outData;
   }

   private static int[] DecompressHuff(HexInputStream his) throws IOException {
      his.skip(-1L);
      int firstByte = his.readU8();
      int dataSize = firstByte & 15;
      if (dataSize != 8 && dataSize != 4) {
         throw new InvalidFileException("Unhandled dataSize " + Integer.toHexString(dataSize));
      } else {
         int decomp_size = getLength(his);
         int treeSize = his.readU8();
         HuffTreeNode.maxInpos = 4 + (treeSize + 1) * 2;
         HuffTreeNode rootNode = new HuffTreeNode();
         rootNode.parseData(his);
         his.setPosition((long)(4 + (treeSize + 1) * 2));
         int[] indata = new int[(int)((long)his.available() - his.getPosition()) / 4];

         for(int i = 0; i < indata.length; ++i) {
            indata[i] = his.readS32();
         }

         int curr_size = 0;
         decomp_size *= dataSize == 8 ? 1 : 2;
         int[] outdata = new int[decomp_size];
         int idx = -1;
         String codestr = "";
         NLinkedList<Integer> code = new NLinkedList();

         while(curr_size < decomp_size) {
            try {
               StringBuilder var10000 = new StringBuilder(String.valueOf(codestr));
               ++idx;
               codestr = var10000.append(Integer.toBinaryString(indata[idx])).toString();
            } catch (ArrayIndexOutOfBoundsException e) {
               throw new InvalidFileException("not enough data.", e);
            }

            while(codestr.length() > 0) {
               code.addFirst(Integer.parseInt(String.valueOf(codestr.charAt(0))));
               codestr = codestr.substring(1);
               Pair<Boolean, Integer> attempt = rootNode.getValue(code.getLast());
               if ((Boolean)attempt.getFirst()) {
                  try {
                     outdata[curr_size++] = (Integer)attempt.getSecond();
                  } catch (ArrayIndexOutOfBoundsException ex) {
                     if ((Integer)code.getFirst().getValue() != 0) {
                        throw ex;
                     }
                  }

                  code.clear();
               }
            }
         }

         if (codestr.length() > 0 || idx < indata.length - 1) {
            while(idx < indata.length - 1) {
               StringBuilder var20 = new StringBuilder(String.valueOf(codestr));
               ++idx;
               codestr = var20.append(Integer.toBinaryString(indata[idx])).toString();
            }

            codestr = codestr.replace("0", "");
            if (codestr.length() > 0) {
               System.out.println("too much data; str=" + codestr + ", idx=" + idx + "/" + indata.length);
            }
         }

         int[] realout;
         if (dataSize == 4) {
            realout = new int[decomp_size / 2];

            for(int i = 0; i < decomp_size / 2; ++i) {
               if ((outdata[i * 2] & 240) > 0 || (outdata[i * 2 + 1] & 240) > 0) {
                  throw new InvalidFileException("first 4 bits of data should be 0 if dataSize = 4");
               }

               realout[i] = (byte)(outdata[i * 2] << 4 | outdata[i * 2 + 1]);
            }
         } else {
            realout = outdata;
         }

         return realout;
      }
   }
}
