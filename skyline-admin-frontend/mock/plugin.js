const templateContent = '<template>\n' +
  '<el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="100px" class="demo-ruleForm">\n' +
  '  <el-form-item label="活动名称" prop="name">\n' +
  '    <el-input v-model="ruleForm.name"></el-input>\n' +
  '  </el-form-item>\n' +
  '  <el-form-item label="活动区域" prop="region">\n' +
  '    <el-select v-model="ruleForm.region" placeholder="请选择活动区域">\n' +
  '      <el-option label="区域一" value="shanghai"></el-option>\n' +
  '      <el-option label="区域二" value="beijing"></el-option>\n' +
  '    </el-select>\n' +
  '  </el-form-item>\n' +
  '  <el-form-item label="活动时间" required>\n' +
  '    <el-col :span="11">\n' +
  '      <el-form-item prop="date1">\n' +
  '        <el-date-picker type="date" placeholder="选择日期" v-model="ruleForm.date1" style="width: 100%;"></el-date-picker>\n' +
  '      </el-form-item>\n' +
  '    </el-col>\n' +
  '    <el-col class="line" :span="2">-</el-col>\n' +
  '    <el-col :span="11">\n' +
  '      <el-form-item prop="date2">\n' +
  '        <el-time-picker placeholder="选择时间" v-model="ruleForm.date2" style="width: 100%;"></el-time-picker>\n' +
  '      </el-form-item>\n' +
  '    </el-col>\n' +
  '  </el-form-item>\n' +
  '  <el-form-item label="即时配送" prop="delivery">\n' +
  '    <el-switch v-model="ruleForm.delivery"></el-switch>\n' +
  '  </el-form-item>\n' +
  '  <el-form-item label="活动性质" prop="type">\n' +
  '    <el-checkbox-group v-model="ruleForm.type">\n' +
  '      <el-checkbox label="美食/餐厅线上活动" name="type"></el-checkbox>\n' +
  '      <el-checkbox label="地推活动" name="type"></el-checkbox>\n' +
  '      <el-checkbox label="线下主题活动" name="type"></el-checkbox>\n' +
  '      <el-checkbox label="单纯品牌曝光" name="type"></el-checkbox>\n' +
  '    </el-checkbox-group>\n' +
  '  </el-form-item>\n' +
  '  <el-form-item label="特殊资源" prop="resource">\n' +
  '    <el-radio-group v-model="ruleForm.resource">\n' +
  '      <el-radio label="线上品牌商赞助"></el-radio>\n' +
  '      <el-radio label="线下场地免费"></el-radio>\n' +
  '    </el-radio-group>\n' +
  '  </el-form-item>\n' +
  '  <el-form-item label="活动形式" prop="desc">\n' +
  '    <el-input type="textarea" v-model="ruleForm.desc"></el-input>\n' +
  '  </el-form-item>\n' +
  '  <el-form-item>\n' +
  '    <el-button type="primary" @click="submitForm(\'ruleForm\')">立即创建</el-button>\n' +
  '    <el-button @click="resetForm(\'ruleForm\')">重置</el-button>\n' +
  '  </el-form-item>\n' +
  '  <div class="my-color">我的颜色来源于自定义样式</div>\n' +
  '</el-form>\n' +
  '</template>\n' +
  '<script>\n' +
  '  export default {\n' +
  '    data() {\n' +
  '      return {\n' +
  '        ruleForm: {\n' +
  '          name: \'\',\n' +
  '          region: \'\',\n' +
  '          date1: \'\',\n' +
  '          date2: \'\',\n' +
  '          delivery: false,\n' +
  '          type: [],\n' +
  '          resource: \'\',\n' +
  '          desc: \'\'\n' +
  '        },\n' +
  '        rules: {\n' +
  '          name: [\n' +
  '            { required: true, message: \'请输入活动名称\', trigger: \'blur\' },\n' +
  '            { min: 3, max: 5, message: \'长度在 3 到 5 个字符\', trigger: \'blur\' }\n' +
  '          ],\n' +
  '          region: [\n' +
  '            { required: true, message: \'请选择活动区域\', trigger: \'change\' }\n' +
  '          ],\n' +
  '          date1: [\n' +
  '            { type: \'date\', required: true, message: \'请选择日期\', trigger: \'change\' }\n' +
  '          ],\n' +
  '          date2: [\n' +
  '            { type: \'date\', required: true, message: \'请选择时间\', trigger: \'change\' }\n' +
  '          ],\n' +
  '          type: [\n' +
  '            { type: \'array\', required: true, message: \'请至少选择一个活动性质\', trigger: \'change\' }\n' +
  '          ],\n' +
  '          resource: [\n' +
  '            { required: true, message: \'请选择活动资源\', trigger: \'change\' }\n' +
  '          ],\n' +
  '          desc: [\n' +
  '            { required: true, message: \'请填写活动形式\', trigger: \'blur\' }\n' +
  '          ]\n' +
  '        }\n' +
  '      };\n' +
  '    },\n' +
  '    methods: {\n' +
  '      submitForm(formName) {\n' +
  '        this.$refs[formName].validate((valid) => {\n' +
  '          if (valid) {\n' +
  '            alert(\'submit!\');\n' +
  '          } else {\n' +
  '            console.log(\'error submit!!\');\n' +
  '            return false;\n' +
  '          }\n' +
  '        });\n' +
  '      },\n' +
  '      resetForm(formName) {\n' +
  '        this.$refs[formName].resetFields();\n' +
  '      },\n' +
  '\t  getConfigData(){\n' +
  '\t\tlet context = this;\n' +
  '\t\tlet data = null;\n' +
  '\t\tthis.$refs[\'ruleForm\'].validate((valid) => {\n' +
  '          if (valid) {\n' +
  '            data = context.ruleForm\n' +
  '          } else {\n' +
  '            data = null;\n' +
  '          }\n' +
  '        });\n' +
  '\t\treturn data;\n' +
  '\t  }\n' +
  '    }\n' +
  '  }\n' +
  '</script>\n' +
  '<style lang="scss" scoped>\n' +
  '.el-form-item__label {\n' +
  '    text-align: right;\n' +
  '    vertical-align: middle;\n' +
  '    float: left;\n' +
  '    font-size: 14px;\n' +
  '    color: red;\n' +
  '    line-height: 40px;\n' +
  '    padding: 0 12px 0 0;\n' +
  '    -webkit-box-sizing: border-box;\n' +
  '    box-sizing: border-box;\n' +
  '}\n' +
  ' .my-color{\n' +
  '\tcolor: #673ab7;\n' +
  ' }\n' +
  '</style>'

const Mock = require('mockjs')

const data = Mock.mock({
  'items|10': [{
    id: '1',
    maintainer: '@sentence(5, 8)',
    classDefine: 'Test.class',
    pluginName: '测试插件',
    overview: '插件描述',
    createTime: '@datetime',
    updateTime: '@datetime',
  }]
})

const data2 = Mock.mock({
  'items|10': [{
    id: '1',
    ver: 'v@integer(1, 5).@integer(0, 3).@integer(1, 50)',
    features: ["高效","高效","高效"],
    pluginVO: data.items[0],
    pageContent: templateContent,
    typeMeta: '{"capableSwitchList": [{"age": 10, "name": "张三"},{"birthday": 1682510430723, "height": 175}],"perpetualResourceList": [{"a": "aaa", "b": "bbbb"}]}',
    'active|1': [true, false],
    size: '@integer(1000000,1000000000)',
    jarUrl: 'file://xxx/xxx/xxx.jar',
    fileKey: 'abc',
    createTime: '@datetime',
    updateTime: '@datetime',
  }]
})

module.exports = [
  {
    url: '/plugin/pageList',
    type: 'get',
    response: config => {
      const items = data.items
      return {
        code: 200,
        data: {
          totalCount: 21,
          data: items
        }
      }
    }
  },
  {
    url: '/plugin/upload',
    type: 'post',
    response: config => {
      return {
        code: 200,
        data: {}
      }
    }
  },
  {
    url: '/plugin/1',
    type: 'delete',
    response: config => {
      return {
        code: 200,
        data: {}
      }
    }
  },
  {
    url: '/plugin/enable/1',
    type: 'post',
    response: config => {
      return {
        code: 200,
        data: {}
      }
    }
  },
  {
    url: '/plugin/disable/1',
    type: 'post',
    response: config => {
      return {
        code: 200,
        data: {}
      }
    }
  },
  {
    url: '/plugin',
    type: 'post',
    response: config => {
      return {
        code: 200,
        data: {}
      }
    }
  },
  {
    url: '/plugin/ver/pageList',
    type: 'get',
    response: config => {
      const items = data2.items
      return {
        code: 200,
        data: {
          totalCount: 21,
          data: items
        }
      }
    }
  },
  {
    url: '/plugin/ver/search',
    type: 'get',
    response: config => {
      const items = data2.items
      return {
        code: 200,
        data: {
          totalCount: 21,
          data: items
        }
      }
    }
  },
  {
    url: '/plugin/ver/enable/1',
    type: 'post',
    response: config => {
      return {
        code: 200,
        data: {}
      }
    }
  },
  {
    url: '/plugin/ver/disable/1',
    type: 'post',
    response: config => {
      return {
        code: 200,
        data: {}
      }
    }
  },
]
