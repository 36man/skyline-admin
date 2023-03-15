package org.apache.skyline.admin.server.config.custom;

import org.bravo.gaia.id.generator.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;

@Component
public class MybatisIdentifierGenerator implements IdentifierGenerator {

    @Autowired
    private IdGenerator<Long> idGenerator;
    @Override
    public Long nextId(Object entity) {
        return idGenerator.generate();
    }
}