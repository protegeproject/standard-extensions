package edu.stanford.smi.protegex.queries_tab;

public class QueryRecord {
   int position;
   String queryName;

    public QueryRecord(int p, String name) {
        position = p;
        queryName = name;
    }

    public String getName() {
        return queryName;
    }

    public int getPosition() {
        return position;
    }
}
