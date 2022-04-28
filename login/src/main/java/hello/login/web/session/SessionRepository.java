package hello.login.web.session;

import hello.login.domain.member.Member;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Repository
public class SessionRepository {

    private Map<String, Member> store = new HashMap<>();

    public String save(Member member) {
        String uuid = UUID.randomUUID().toString();
        store.put(uuid, member);
        return uuid;
    }

    public Member findBySessionId(String sessionId) {
        return store.get(sessionId);
    }
}
