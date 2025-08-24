package com.example.demo.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.titlever.TitleVersion;

@Repository
class TitleVersionRepositoryCustomImpl implements TitleVersionRepositoryCustom {

    @Autowired
    private JdbcTemplate jdbc;

    @Override
    @Transactional
    public int bulkInsertIgnoreConflicts(List<TitleVersion> rows) {
        if (rows.isEmpty())
            return 0;

        final String sql = """
                INSERT INTO title_version (
                  issue_date, identifier, title,
                  amendment_date, name, part,
                  substantive, removed, subpart,
                  type, date
                )
                SELECT i, n, t,  a, nm, p, s, r, sp, ty, d
                FROM unnest(
                  ?::text[], ?::text[], ?::text[],
                  ?::text[], ?::text[], ?::text[],
                  ?::boolean[], ?::boolean[], ?::text[],
                  ?::text[], ?::text[]
                ) AS v(i,n,t,a,nm,p,s,r,sp,ty,d)
                ON CONFLICT (issue_date, identifier, title) DO NOTHING
                """;

        // Build arrays for each column (match the order in SQL)
        int N = rows.size();
        String[] issueDate = new String[N];
        String[] identifier = new String[N];
        String[] title = new String[N];
        String[] amendmentDate = new String[N];
        String[] name = new String[N];
        String[] part = new String[N];
        Boolean[] substantive = new Boolean[N];
        Boolean[] removed = new Boolean[N];
        String[] subpart = new String[N];
        String[] type = new String[N];
        String[] date = new String[N];

        for (int i = 0; i < N; i++) {
            var r = rows.get(i);
            issueDate[i] = r.getIssue_date();
            identifier[i] = r.getIdentifier();
            title[i] = r.getTitle();
            amendmentDate[i] = r.getAmendment_date();
            name[i] = r.getName();
            part[i] = r.getPart();
            substantive[i] = r.getSubstantive();
            removed[i] = r.getRemoved();
            subpart[i] = r.getSubpart();
            type[i] = r.getType();
            date[i] = r.getDate();
        }

        return jdbc.update(con -> {
            var ps = con.prepareStatement(sql);
            ps.setArray(1, con.createArrayOf("text", issueDate));
            ps.setArray(2, con.createArrayOf("text", identifier));
            ps.setArray(3, con.createArrayOf("text", title));
            ps.setArray(4, con.createArrayOf("text", amendmentDate));
            ps.setArray(5, con.createArrayOf("text", name));
            ps.setArray(6, con.createArrayOf("text", part));
            ps.setArray(7, con.createArrayOf("boolean", substantive));
            ps.setArray(8, con.createArrayOf("boolean", removed));
            ps.setArray(9, con.createArrayOf("text", subpart));
            ps.setArray(10, con.createArrayOf("text", type));
            ps.setArray(11, con.createArrayOf("text", date));
            return ps;
        });
    }
}