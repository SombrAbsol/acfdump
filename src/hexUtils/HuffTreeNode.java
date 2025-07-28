package hexUtils;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.LinkedList;

class HuffTreeNode {
   protected static int maxInpos = 0;
   protected HuffTreeNode node0;
   protected HuffTreeNode node1;
   protected int data = -1;

   protected Pair<Boolean, Integer> getLast(LinkedList<Integer> code) throws InvalidFileException {
      Pair<Boolean, Integer> outData = new Pair<>();
      outData.setSecond(this.data);
      if (code != null) {
         if ((Integer)code.getLast() > 1) {
            throw new InvalidFileException("The list should be a list of bytes < 2. got: " + code.getLast());
         } else {
            int c = (Integer)code.getLast();
            HuffTreeNode n = c == 0 ? this.node0 : this.node1;
            if (n == null) {
               outData.setFirst(false);
            }

            code.removeLast();
            return n.getLast(code);
         }
      } else {
         outData.setFirst(this.node0 == null && this.node1 == null && this.data >= 0);
         return outData;
      }
   }

   protected int getLast(String code) throws InvalidFileException {
      LinkedList<Integer> c = new LinkedList<>();

      char[] var6;
      for(char ch : var6 = code.toCharArray()) {
         c.addFirst(Integer.valueOf(ch));
      }

      Pair<Boolean, Integer> attempt = this.getLast(c);
      return (Boolean)attempt.getFirst() ? (Integer)attempt.getSecond() : -1;
   }

   protected void parseData(HexInputStream his) throws IOException {
      this.node0 = new HuffTreeNode();
      this.node1 = new HuffTreeNode();
      long currPos = his.getPosition();
      int b = his.readU8();
      long offset = (long)(b & 63);
      boolean end0 = (b & 128) > 0;
      boolean end1 = (b & 64) > 0;
      his.setPosition(currPos - (currPos & 1L) + offset * 2L + 2L);
      if (his.getPosition() < (long)maxInpos) {
         if (end0) {
            this.node0.data = his.readU8();
         } else {
            this.node0.parseData(his);
         }
      }

      his.setPosition(currPos - (currPos & 1L) + offset * 2L + 2L + 1L);
      if (his.getPosition() < (long)maxInpos) {
         if (end1) {
            this.node1.data = his.readU8();
         } else {
            this.node1.parseData(his);
         }
      }

      his.setPosition(currPos);
   }

   public String toString() {
      return this.data < 0 ? "<" + this.node0.toString() + ", " + this.node1.toString() + ">" : "[" + Integer.toHexString(this.data) + "]";
   }

   protected int getDepth() {
      return this.data < 0 ? 0 : 1 + Math.max(this.node0.getDepth(), this.node1.getDepth());
   }
}
