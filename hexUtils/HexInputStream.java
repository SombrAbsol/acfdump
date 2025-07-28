package hexUtils;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;

public class HexInputStream {
   private volatile InputStream dis;
   private volatile long currPos;
   private Stack positionStack;

   public long getPosition() {
      return this.currPos;
   }

   public void setPosition(long newPos) throws IOException {
      this.skip(newPos - this.currPos);
   }

   public void goTo(long pos) throws IOException {
      this.setPosition(pos);
   }

   public HexInputStream(InputStream baseInputStream) {
      this.dis = baseInputStream;
      this.currPos = 0L;
      this.positionStack = new Stack();
   }

   public HexInputStream(String filename) throws FileNotFoundException {
      this.dis = new DataInputStream(new FileInputStream(new File(filename)));
      this.currPos = 0L;
      this.positionStack = new Stack();
   }

   public int available() throws IOException {
      return this.dis.available();
   }

   public int read() throws IOException {
      int b = this.dis.read();
      if (b != -1) {
         ++this.currPos;
      }

      return b;
   }

   public int[] readBytes(int length) throws IOException {
      int[] data = new int[length];

      for(int i = 0; i < length; ++i) {
         data[i] = this.readU8();
      }

      return data;
   }

   public int readU8() throws IOException {
      int b = this.dis.read();
      ++this.currPos;
      return b;
   }

   public short readS16() throws IOException {
      short word = 0;

      for(int i = 0; i < 2; ++i) {
         word = (short)(word | this.readU8() << 8 * i);
      }

      return word;
   }

   public short readlS16() throws IOException {
      short word = 0;

      for(int i = 0; i < 2; ++i) {
         word = (short)(word << 8 | this.readU8());
      }

      return word;
   }

   public int readU16() throws IOException {
      int word = 0;

      for(int i = 0; i < 2; ++i) {
         word |= this.readU8() << 8 * i;
      }

      return word;
   }

   public int readlU16() throws IOException {
      int word = 0;

      for(int i = 0; i < 2; ++i) {
         word = word << 8 | this.readU8();
      }

      return word;
   }

   public int readS32() throws IOException {
      int dword = 0;

      for(int i = 0; i < 4; ++i) {
         dword |= this.readU8() << 8 * i;
      }

      return dword;
   }

   public int readlS32() throws IOException {
      int dword = 0;

      for(int i = 0; i < 4; ++i) {
         dword = dword << 8 | this.readU8();
      }

      return dword;
   }

   public long readU32() throws IOException {
      long dword = 0L;

      for(int i = 0; i < 4; ++i) {
         dword |= (long)(this.readU8() << 8 * i);
      }

      return dword;
   }

   public long readlU32() throws IOException {
      long dword = 0L;

      for(int i = 0; i < 4; ++i) {
         dword = dword << 8 | (long)this.readU8();
      }

      return dword;
   }

   public long readS64() throws IOException {
      long qword = 0L;

      for(int i = 0; i < 8; ++i) {
         qword |= (long)(this.readU8() << 8 * i);
      }

      return qword;
   }

   public long readlS64() throws IOException {
      long qword = 0L;

      for(int i = 0; i < 8; ++i) {
         qword = qword << 8 | (long)this.readU8();
      }

      return qword;
   }

   public short peekU8() throws IOException {
      try {
         short b = (short)this.readU8();
         this.skip(-1L);
         return b;
      } catch (EOFException var2) {
         return -1;
      }
   }

   public short peekS16() throws IOException {
      try {
         short s = this.readS16();
         this.skip(-2L);
         return s;
      } catch (EOFException var2) {
         return -1;
      }
   }

   public short peeklS16() throws IOException {
      try {
         short s = this.readlS16();
         this.skip(-2L);
         return s;
      } catch (EOFException var2) {
         return -1;
      }
   }

   public int peekU16() throws IOException {
      try {
         int s = this.readU16();
         this.skip(-2L);
         return s;
      } catch (EOFException var2) {
         return -1;
      }
   }

   public int peeklU16() throws IOException {
      try {
         int s = this.readlU16();
         this.skip(-2L);
         return s;
      } catch (EOFException var2) {
         return -1;
      }
   }

   public int peekS32() throws IOException {
      try {
         int s = this.readS32();
         this.skip(-4L);
         return s;
      } catch (EOFException var2) {
         return -1;
      }
   }

   public int peeklS32() throws IOException {
      try {
         int s = this.readlS32();
         this.skip(-4L);
         return s;
      } catch (EOFException var2) {
         return -1;
      }
   }

   public long peekU32() throws IOException {
      try {
         long s = this.readU32();
         this.skip(-4L);
         return s;
      } catch (EOFException var3) {
         return -1L;
      }
   }

   public long peeklU32() throws IOException {
      try {
         long s = this.readlU32();
         this.skip(-4L);
         return s;
      } catch (EOFException var3) {
         return -1L;
      }
   }

   public long peekS64() throws IOException {
      try {
         long s = this.readS64();
         this.skip(-8L);
         return s;
      } catch (EOFException var3) {
         return -1L;
      }
   }

   public long peeklS64() throws IOException {
      try {
         long s = this.readlS64();
         this.skip(-8L);
         return s;
      } catch (EOFException var3) {
         return -1L;
      }
   }

   public String readString(int length) throws IOException {
      StringBuffer sbuf = new StringBuffer(length);

      for(int i = 0; i < length; ++i) {
         sbuf.append((char)this.readU8());
      }

      return sbuf.toString();
   }

   public String read0TerminatedString(int totlength) throws IOException {
      if (totlength != -1) {
         StringBuffer sbuf = new StringBuffer();
         boolean read0 = false;

         for(int i = 0; i < totlength; ++i) {
            if (this.peekU8() == 0) {
               read0 = true;
            }

            char c = (char)this.readU8();
            if (!read0) {
               sbuf.append(c);
            }
         }

         return sbuf.toString();
      } else {
         StringBuffer sbuf = new StringBuffer();

         while(this.peekU8() != 0) {
            sbuf.append((char)this.readU8());
         }

         this.readU8();
         return sbuf.toString();
      }
   }

   public void close() throws IOException {
      this.dis.close();
   }

   public void skip(long n) throws IOException {
      this.dis.skip(n);
      this.currPos += n;
   }

   public void reset() throws IOException {
      this.goTo(0L);
   }

   public void savePosition() {
      this.positionStack.push(this.currPos);
   }

   public void loadPosition() throws IOException {
      if (!this.positionStack.isEmpty()) {
         long pos = (Long)this.positionStack.peek();
         this.positionStack.pop();
         this.goTo(pos);
      }

   }
}
