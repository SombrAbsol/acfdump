package hexUtils;

public class Triplet<T, U, V> {
   private T first;
   private U second;
   private V third;

   public Triplet(T first, U second, V third) {
      this.first = first;
      this.second = second;
      this.third = third;
   }

   public Triplet() {
   }

   public T getFirst() {
      return this.first;
   }

   public void setFirst(T first) {
      this.first = first;
   }

   public U getSecond() {
      return this.second;
   }

   public void setSecond(U second) {
      this.second = second;
   }

   public V getThird() {
      return this.third;
   }

   public void setThird(V third) {
      this.third = third;
   }

   public boolean allSet() {
      return this.first != null && this.second != null && this.third != null;
   }

   public String toString() {
      return "<" + (this.getFirst() == null ? "-null-" : this.getFirst().toString()) + "," +
                   (this.getSecond() == null ? "-null-" : this.getSecond().toString()) + "," +
                   (this.getThird() == null ? "-null-" : this.getThird().toString()) + ">";
   }
}
