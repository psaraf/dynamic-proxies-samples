package eu.javaspecialists.books.dynamicproxies.ch02;


import eu.javaspecialists.books.dynamicproxies.ch02.Listing2_1.Proxies;
import org.junit.Test;

import java.util.Objects;

import static org.junit.Assert.*;

public class Listing2_3 {
    // tag::A[]
    // subject
    public interface A {
    }
    // end::A[]

    static
    // tag::B[]
    // real subject
    public final class B implements A {
        private final int i;
        public B(int i) { this.i = i; }
        public boolean equals(Object o) { // auto-generated by IntelliJ
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            B b = (B) o;

            if (i != b.i) return false;

            return true;
        }
        public int hashCode() { // auto-generated by IntelliJ
            return i;
        }
    }
    // end::B[]
    static
    // tag::C[]
    // proxy
    public final class C implements A {
        private final A a;
        public C(A a) {
            this.a = Objects.requireNonNull(a);
        }
        public boolean equals(Object o) { // auto-generated by IntelliJ
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            C c = (C) o;

            if (!a.equals(c.a)) return false;

            return true;
        }
        public int hashCode() { // auto-generated by IntelliJ
            return a.hashCode();
        }
    }
    // end::C[]
    static
    // proxy
    // tag::D[]
    public final class D implements A {
        private final A a;
        public D(A a) {
            this.a = Objects.requireNonNull(a);
        }
        public boolean equals(Object o) {
            return a.equals(o);
        }

        public int hashCode() {
            return a.hashCode();
        }
    }
    // end::D[]

    public static class Tester {
        private static final boolean USE_DYNAMIC_PROXIES = false;

        private A[] create() {
            A temp;
            return new A[]{
                temp = new B(42),
                makeProxy(temp),
                temp = makeProxy(temp),
                temp = makeProxy(temp),
                makeProxy(temp),
                temp = new B(57),
                makeProxy(temp),
                makeProxy(temp),
            };
        }

        @Test
        public void reflexive() {
            for (A x : create()) {
                assertTrue(x.equals(x));
            }
        }

        @Test
        public void symmetric() {
            A[] objects = create();

            for (int i = 0; i < objects.length - 1; i++) {
                for (int j = i + 1; j < objects.length; j++) {
                    A x = objects[i];
                    A z = objects[j];
                    testSymmetry(i + "," + j, x, z);
                }
            }
        }

        private void testSymmetry(String description, A x, A z) {
            assertSame(description, x.equals(z), z.equals(x));
        }

        @Test
        public void transitive() {
            A[] objects = create();

            for (int i = 0; i < objects.length; i++) {
                A x = objects[i];
                for (int j = 0; j < objects.length; j++) {
                    A y = objects[j];
                    for (int k = 0; k < objects.length; k++) {
                        A z = objects[k];
                        testTransitivity(i + "," + j + "," + k, x, y, z);
                    }
                }
            }
        }

        private void testTransitivity(String description, A x, A y, A z) {
            if (x.equals(y) && y.equals(z))
                assertTrue(description, x.equals(z));
        }

        @Test
        public void nullEquals() {
            for (A x : create()) {
                assertFalse(x.equals(null));
            }
        }


        @Test
        public void proxyEqualsProxy() {
            A real = new B(42);
            A proxy1 = makeProxy(real);
            A proxy2 = makeProxy(real);
            assertTrue(proxy1.equals(proxy2));
            assertTrue(proxy2.equals(proxy1));
            assertTrue(proxy1.equals(proxy1));
            assertTrue(proxy2.equals(proxy2));
        }

        private A makeProxy(A a) {
            return USE_DYNAMIC_PROXIES ? Proxies.makeSimpleProxy(A.class
                , a) :
                new C(a);
        }

        @Test
        public void proxyEqualsReal() {
            B b = new B(42);
            A c1 = makeProxy(b);
            assertTrue(c1.equals(b));
        }

        @Test
        public void realEqualsProxy() {
            A real = new B(42);
            A proxy = makeProxy(real);
            assertTrue(real.equals(proxy));
            assertTrue(real.equals(real));
        }

        @Test
        public void cascadingProxyEquals() {
            A real = new B(42);
            A proxy1 = makeProxy(real);
            A proxy2 = makeProxy(proxy1);
            A proxy3 = makeProxy(proxy2);
            assertTrue(real.equals(proxy1));
            assertTrue(real.equals(proxy2));
            assertTrue(real.equals(proxy3));
            assertTrue(proxy1.equals(real));
            assertTrue(proxy2.equals(real));
            assertTrue(proxy3.equals(real));
            assertTrue(proxy1.equals(proxy2));
            assertTrue(proxy1.equals(proxy3));
            assertTrue(proxy2.equals(proxy1));
            assertTrue(proxy2.equals(proxy3));
            assertTrue(proxy3.equals(proxy1));
            assertTrue(proxy3.equals(proxy2));
            assertTrue(proxy1.equals(proxy1));
            assertTrue(proxy2.equals(proxy2));
            assertTrue(proxy3.equals(proxy3));
        }

        @Test
        public void cascadingProxyEqualsWithD() {
            A real = new B(42);
            A proxy1 = makeProxy(real);
            A proxy2 = new D(proxy1);
            A proxy3 = makeProxy(proxy2);
            assertTrue(real.equals(proxy1));
            assertTrue(real.equals(proxy2));
            assertTrue(real.equals(proxy3));
            assertTrue(proxy1.equals(real));
            assertTrue(proxy2.equals(real));
            assertTrue(proxy3.equals(real));
            assertTrue(proxy1.equals(proxy2));
            assertTrue(proxy1.equals(proxy3));
            assertTrue(proxy2.equals(proxy1));
            assertTrue(proxy2.equals(proxy3));
            assertTrue(proxy3.equals(proxy1));
            assertTrue(proxy3.equals(proxy2));
            assertTrue(proxy1.equals(proxy1));
            assertTrue(proxy2.equals(proxy2));
            assertTrue(proxy3.equals(proxy3));
        }
    }
}
