package study.querydsl.entity;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import study.querydsl.dto.MemberDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
class MemberTest {

    @Autowired
    EntityManager em;

    @Test
    public void testEntity(){
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



}