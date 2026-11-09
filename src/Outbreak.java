import java.util.*;

/**
 * Mission 5. Dynamic programming on the tree of infections. Root the tree in person 0.
 * Input.Tree.neighbours() gives the neighbours of every person. The number of people is at most about 100, so a recursive DFS is fine here.
 */
public class Outbreak {

    /**
     * Choose people to test so that for every link (somebody infected somebody) at least one of the two is tested,
     * and the total cost of testing is as small as possible. cost[v] is the price of testing person v.
     * Returns tested[v] = true if person v is tested.
     *
     * State for every person v (looking only at v and everything below it): the smallest cost when v IS tested, and
     * the smallest cost when v is NOT tested. What must then be true about the children of v in each case?
     * Do not forget to rebuild the answer (who exactly is tested), not only the cost: the picture needs it.
     * Main calls this twice: with all costs equal to 1 (the line "Tests needed") and with the real costs ("Cheapest testing").
     */
    public static boolean[] cheapestTests(int n, int[][] nb, int[] cost) {
        int[] parent = new int[n];
        int[] order = new int[n];
        Arrays.fill(parent, -1);
        boolean[] seen = new boolean[n];
        ArrayDeque<Integer> queue = new ArrayDeque<>();
        int size = 0;
        queue.add(0);
        seen[0] = true;
        while (!queue.isEmpty()) {
            int v = queue.poll();
            order[size++] = v;
            for (int u : nb[v]) {
                if (!seen[u]) { seen[u] = true; parent[u] = v; queue.add(u); }
            }
        }
        long[] in = new long[n];
        long[] out = new long[n];
        for (int idx = size - 1; idx >= 0; idx--) {
            int v = order[idx];
            in[v] += cost[v];
            for (int u : nb[v]) {
                if (u == parent[v]) continue;
                in[v] += Math.min(in[u], out[u]);
                out[v] += in[u];
            }
        }
        boolean[] tested = new boolean[n];
        boolean[] state = new boolean[n];
        state[0] = in[0] <= out[0];
        for (int idx = 0; idx < size; idx++) {
            int v = order[idx];
            tested[v] = state[v];
            for (int u : nb[v]) {
                if (u == parent[v]) continue;
                state[u] = state[v] ? in[u] <= out[u] : true;
            }
        }
        return tested;
    }

    /**
     * The number of links on the longest chain of infections, that is the longest path in the tree (counted in links).
     * A path goes up from one person and down to another; for every person you need the two longest ways down.
     * Example: a line 0-1-2-3 has chain 3; a star with centre 0 and leaves 1, 2, 3 has chain 2.
     */
    public static int longestChain(int n, int[][] nb) {
        int[] parent = new int[n];
        int[] order = new int[n];
        Arrays.fill(parent, -1);
        boolean[] seen = new boolean[n];
        ArrayDeque<Integer> queue = new ArrayDeque<>();
        int size = 0;
        queue.add(0);
        seen[0] = true;
        while (!queue.isEmpty()) {
            int v = queue.poll();
            order[size++] = v;
            for (int u : nb[v]) {
                if (!seen[u]) { seen[u] = true; parent[u] = v; queue.add(u); }
            }
        }
        int[] down = new int[n];
        int best = 0;
        for (int idx = size - 1; idx >= 0; idx--) {
            int v = order[idx];
            int first = 0, second = 0;
            for (int u : nb[v]) {
                if (u == parent[v]) continue;
                int len = down[u] + 1;
                if (len > first) { second = first; first = len; }
                else if (len > second) second = len;
            }
            down[v] = first;
            best = Math.max(best, first + second);
        }
        return best;
    }
}
