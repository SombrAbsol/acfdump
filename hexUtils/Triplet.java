package hexUtils;

public class Triplet {
   private Object first;
   private Object second;
   private Object third;

   public Triplet(Object t, Object u, Object v) {
      this.first = t;
      this.second = u;
      this.third = v;
   }

   public Triplet() {
   }

   public Object getFirst() {
      return this.first;
   }

   public void setFirst(Object first) {
      this.first = first;
   }

   public Object getSecond() {
      return this.second;
   }

   public void setSecond(Object second) {
      this.second = second;
   }

   public Object getThird() {
      return this.third;
   }

   public void setThird(Object third) {
      this.third = third;
   }

   public boolean allSet() {
      return this.first != null && this.second != null && this.third != null;
   }

   public String toString() {
      return "<" + (this.getFirst() == null ? "-null-" : this.getFirst().toString()) + "," + (this.getSecond() == null ? "-null-" : this.getSecond().toString()) + "," + (this.getThird() == null ? "-null-" : this.getThird().toString()) + ">";
   }
}
