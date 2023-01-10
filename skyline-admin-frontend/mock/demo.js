const Mock = require('mockjs')

const data = Mock.mock({
  templateContent: '' +
    '{\n' +
    '    template: \'<el-form ref="form" :model="form" label-width="80px">\\n\' +\n' +
    '      \'  <el-form-item label="活动名称">\\n\' +\n' +
    '      \'    <el-input v-model="form.name"></el-input>\\n\' +\n' +
    '      \'  </el-form-item>\\n\' +\n' +
    '      \'  <el-form-item label="活动区域">\\n\' +\n' +
    '      \'    <el-select v-model="form.region" placeholder="请选择活动区域">\\n\' +\n' +
    '      \'      <el-option label="区域一" value="shanghai"></el-option>\\n\' +\n' +
    '      \'      <el-option label="区域二" value="beijing"></el-option>\\n\' +\n' +
    '      \'    </el-select>\\n\' +\n' +
    '      \'  </el-form-item>\\n\' +\n' +
    '      \'  <el-form-item label="活动时间">\\n\' +\n' +
    '      \'    <el-col :span="11">\\n\' +\n' +
    '      \'      <el-date-picker type="date" placeholder="选择日期" v-model="form.date1" style="width: 100%;"></el-date-picker>\\n\' +\n' +
    '      \'    </el-col>\\n\' +\n' +
    '      \'    <el-col class="line" :span="2">-</el-col>\\n\' +\n' +
    '      \'    <el-col :span="11">\\n\' +\n' +
    '      \'      <el-time-picker placeholder="选择时间" v-model="form.date2" style="width: 100%;"></el-time-picker>\\n\' +\n' +
    '      \'    </el-col>\\n\' +\n' +
    '      \'  </el-form-item>\\n\' +\n' +
    '      \'  <el-form-item label="即时配送">\\n\' +\n' +
    '      \'    <el-switch v-model="form.delivery"></el-switch>\\n\' +\n' +
    '      \'  </el-form-item>\\n\' +\n' +
    '      \'  <el-form-item label="活动性质">\\n\' +\n' +
    '      \'    <el-checkbox-group v-model="form.type">\\n\' +\n' +
    '      \'      <el-checkbox label="美食/餐厅线上活动" name="type"></el-checkbox>\\n\' +\n' +
    '      \'      <el-checkbox label="地推活动" name="type"></el-checkbox>\\n\' +\n' +
    '      \'      <el-checkbox label="线下主题活动" name="type"></el-checkbox>\\n\' +\n' +
    '      \'      <el-checkbox label="单纯品牌曝光" name="type"></el-checkbox>\\n\' +\n' +
    '      \'    </el-checkbox-group>\\n\' +\n' +
    '      \'  </el-form-item>\\n\' +\n' +
    '      \'  <el-form-item label="特殊资源">\\n\' +\n' +
    '      \'    <el-radio-group v-model="form.resource">\\n\' +\n' +
    '      \'      <el-radio label="线上品牌商赞助"></el-radio>\\n\' +\n' +
    '      \'      <el-radio label="线下场地免费"></el-radio>\\n\' +\n' +
    '      \'    </el-radio-group>\\n\' +\n' +
    '      \'  </el-form-item>\\n\' +\n' +
    '      \'  <el-form-item label="活动形式">\\n\' +\n' +
    '      \'    <el-input type="textarea" v-model="form.desc"></el-input>\\n\' +\n' +
    '      \'  </el-form-item>\\n\' +\n' +
    '      \'  <el-form-item>\\n\' +\n' +
    '      \'    <el-button type="primary" \\@click="onSubmit">立即创建</el-button>\\n\' +\n' +
    '      \'    <el-button>取消</el-button>\\n\' +\n' +
    '      \'  </el-form-item>\\n\' +\n' +
    '      \'</el-form>\',\n' +
    '    data() {\n' +
    '      return {\n' +
    '        form: {\n' +
    '          name: \'\',\n' +
    '          region: \'\',\n' +
    '          date1: \'\',\n' +
    '          date2: \'\',\n' +
    '          delivery: false,\n' +
    '          type: [],\n' +
    '          resource: \'\',\n' +
    '          desc: \'\'\n' +
    '        }\n' +
    '      }\n' +
    '    },\n' +
    '    methods: {\n' +
    '      onSubmit() {\n' +
    '        console.log(\'submit!\');\n' +
    '      }\n' +
    '    }\n' +
    '  }'
})
module.exports = [
  {
    url: '/vue-admin-template/demo/demo',
    type: 'get',
    response: config => {
      return {
        code: 20000,
        data: data
      }
    }
  }
]
