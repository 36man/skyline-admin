package org.apache.skyline.admin.server.config.custom;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import org.bravo.gaia.id.generator.IdGenerator;
import org.springframework.stereotype.Component;

@Component
public class MybatisIdGenerator implements IdentifierGenerator {

    private final IdGenerator<Long> idGenerator;

    public MybatisIdGenerator(IdGenerator<Long> idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public Long nextId(Object entity) {
        return idGenerator.generate();
    }
}