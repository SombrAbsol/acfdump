package hexUtils;

import java.io.IOException;
import java.io.InputStream;

public class ArrayInputStream extends InputStream {
   private volatile int[] data;
   private int currPos;
   private int markpos = 0;

   public ArrayInputStream(int[] data) {
      this.data = data;
      this.currPos = 0;
   }

   public int read() throws IOException {
      return this.currPos < this.data.length ? this.data[this.currPos++] : -1;
   }

   public long skip(long n) throws IOException {
      int newPos = (int)Math.min(Math.max((long)this.currPos + n, 0L), (long)this.data.length);
      int diff = newPos - this.currPos;
      this.currPos = newPos;
      return (long)diff;
   }

   public Object clone() {
      ArrayInputStream ais = new ArrayInputStream((int[])this.data.clone());
      ais.currPos = this.currPos;
      return ais;
   }

   public void close() {
      this.data = null;
      this.currPos = -1;
   }

   public void mark(int readlimit) {
      this.markpos = this.currPos;
   }

   public void reset() {
      this.currPos = this.markpos;
   }

   public boolean markSupported() {
      return true;
   }

   public int available() {
      return this.data.length - this.currPos;
   }

   public void finalize() {
      this.close();
   }
}
