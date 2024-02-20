package hello.servlet.domain.member;

import hello.servlet.domain.Member;
import hello.servlet.domain.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class MemberRepositoryTest {
    MemberRepository memberRepository = MemberRepository.getInstance();

    @AfterEach
    void afterEach(){
        memberRepository.clearStore();
    }

    @Test
    void save() {
        Member member = new Member("Hello", 20);
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId());
        assertThat(findMember).isEqualTo(savedMember);
    }

    @Test
    void findAll(){
        Member mem1 = new Member("m1", 20);
        Member mem2 = new Member("m2", 20);

        memberRepository.save(mem1);
        memberRepository.save(mem2);

        List<Member> result = memberRepository.findAll();

        assertThat(result.size()).isEqualTo(2);
        Assertions.assertThat(result).contains(mem1,mem2);
    }
}
