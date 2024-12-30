package study.querydsl;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;

import java.util.List;

import static study.querydsl.entity.QMember.*;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory;

    @BeforeEach
    public void before(){
        queryFactory  = new JPAQueryFactory(em);
        Team team = new Team("teamA");
        Team teamB = new Team("teamB");

        em.persist(team);
        em.persist(teamB);

        Member member1 = new Member("member1",10,team);
        Member member2 = new Member("member2",10,team);
        Member member3 = new Member("member3",10,teamB);
        Member member4 = new Member("member4",10,teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        //초기화
        em.flush();
        em.clear();

        List<Member> members = em.createQuery("select m from Member m", Member.class).getResultList();

        for(Member member : members){
            System.out.println("member = " + member);
            System.out.println("-> member.team" + member.getTeam());

        }
    }

    @Test
    public void startJPQL(){
        //member1을 찾아라.
        Member findMember = em.createQuery("select m from Member m " +
                        "where m.username =:username", Member.class)
                .setParameter("username", "member1")
                .getSingleResult();

        Assertions.assertThat(findMember.getUsername()).isEqualTo("member1");

    }

    @Test
    public void startQuerydsl(){

        QMember m = new QMember("m");
        QMember m2 = member; // 위에 m이랑 같은 로직

        Member findMember = queryFactory
                    .select(member)
                    .from(member)
                    .where(member.username.eq("member1"))
                    .fetchOne();

        Assertions.assertThat(findMember.getUsername()).isEqualTo("member1");

    }

    @Test
    public void search(){
        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1").and(member.age.eq(10)))
                .fetchOne();

        Assertions.assertThat(findMember.getUsername()).isEqualTo("member1");

    }

    @Test
    public void searchAndParam(){
        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1"),member.age.eq(10))
                .fetchOne();

        Assertions.assertThat(findMember.getUsername()).isEqualTo("member1");

    }

    @Test
    public void resultFetch(){
       /* List<Member> fetch = queryFactory
                .selectFrom(member)
                .fetch();

        Member member1 = queryFactory
                .selectFrom(member)
                .fetchOne();

        Member member2 = queryFactory
                .selectFrom(member)
                .fetchFirst(); */

        QueryResults<Member> memberQueryResults = queryFactory
                .selectFrom(member)
                .fetchResults();

        memberQueryResults.getTotal();
        List<Member> members = memberQueryResults.getResults();

        long total = queryFactory
                .selectFrom(member)
                .fetchCount();

        //memberQueryResults.get

        //Assertions.assertThat(findMember.getUsername()).isEqualTo("member1");

    }
}
