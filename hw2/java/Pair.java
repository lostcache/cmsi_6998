public record Pair<A, B>(A first, B second) {
    public A first() {
        return first;
    }

    public B second() {
        return second;
    }
}
