package hello.jdbc.repository;

import com.zaxxer.hikari.HikariDataSource;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static hello.jdbc.connection.ConnectionConst.*;


@Slf4j
class MemberRepositoryV1Test {

	MemberRepositoryV1 repository;

	@BeforeEach
	void beforeEach() {
		//기본 DirverManager - 항상 새로운 커넥션 획득
//		DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);

		//커넥션 풀링
		HikariDataSource dataSource = new HikariDataSource();
		dataSource.setJdbcUrl(URL);
		dataSource.setUsername(USERNAME);
		dataSource.setPassword(PASSWORD);

		repository = new MemberRepositoryV1(dataSource);
	}

	@Test
	void crud() throws SQLException {
		//save
		Member member = new Member("memberV1", 1000);
		repository.save(member);

		//findById
		Member findMember = repository.findById(member.getMemberId());
		log.info("findMember={}", findMember);
		Assertions.assertThat(findMember).isEqualTo(member);

		//update: money: 1000 -> 2000
		repository.update(member.getMemberId(), 2000);
		Member updatedMember = repository.findById(member.getMemberId());
		Assertions.assertThat(2000).isEqualTo(updatedMember.getMoney());

		//delete
		repository.delete(member.getMemberId());
		Assertions.assertThatThrownBy(() -> repository.findById(member.getMemberId()))
				.isInstanceOf(NoSuchElementException.class);
	}

}