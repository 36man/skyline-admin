package org.apache.skyline.admin.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Preconditions;

/**
 * @author hejianbing
 * @version @Id: PluginVersionController.java, v 0.1 2023年01月06日 10:56 hejianbing Exp $
 */
@RestController
@RequestMapping("/plugin/version")
public class PluginVersionController {

    @GetMapping
    public String test(){

        //PredicateUtils.allPredicate()
        String str = null;
        Preconditions.checkArgument(null!=str, "plugin define not found");

        return "test";
    }
}