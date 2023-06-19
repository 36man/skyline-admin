/* eslint-disable */
<template>
  <el-dialog title="插件链配置" :visible.sync="dialogDisplay">
    <el-form :model="form" ref="apiForm">
      <el-tabs v-model="editableTabsValue" type="card" editable @edit="handleTabsEdit">
        <el-tab-pane
          :key="item.name"
          v-for="(item, index) in editableTabs"
          :label="item.name"
          :name="item.name"
        >
          <el-form-item label="匹配规则" :label-width="formLabelWidth" prop="matchCondition" :rules="[
          { required: true, message: '请输入匹配规则', trigger: 'blur' }, { min: 1, max: 50, message: '长度在 1 到 50 个字符', trigger: 'blur' }]">
            <el-input size="small" v-model="form.matchCondition" autocomplete="off"></el-input>
          </el-form-item>
        </el-tab-pane>
      </el-tabs>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogDisplay = false">取 消</el-button>
      <el-button type="primary" @click="submitForm">确 定</el-button>
    </div>
    <!-- plugin select -->
    <plugin-select v-model="pluginSelectVisible" @select="onSelectRow"/>
  </el-dialog>
</template>

<script>
  import { create,update } from '@/api/api'
  import PluginSelect from './pluginSelect.vue'
	export default {
		name: "apiForm",
    components: { PluginSelect },
    props: {
      value: {
        type: Boolean,
        default: false
      },
      apiData: {
        type: Object,
        default: {}
      }
    },
    data(){
		  return {
        dialogDisplay: false,
        formLabelWidth: '120px',
        pluginSelectVisible: false,
        form: {},
        editableTabsValue: '第1阶段',
        editableTabs: [{
          name: "第1阶段",
          index: 1,
          contents: []
        }],
        editableTabIndex: 1,
      }
    },
    watch: {
      dialogDisplay(newValue){
        if(!newValue){
          this.$emit('input', newValue)
        }
      },
      value(newValue){
        if(newValue) {
          this.dialogDisplay = true;
        }
      },
      apiData(newValue) {
        this.resetForm(newValue.id, newValue.pluginList)
      },
    },
    methods: {
      onSelectRow(row){
        console.log(row)
      },
      tabAdd(){
        let tab = {
          name: this.tabNextName(),
          contents: []
        }
        this.editableTabs.push(tab);
        this.editableTabsValue = tab.name;
      },
      tabRemove(targetName){
        let tabs = this.editableTabs;
        let activeName = this.editableTabsValue;
        //删除的是当前打开的tab，重新选择附近的一个为当前tab
        if (activeName === targetName) {
          tabs.forEach((tab, index) => {
            if (tab.name === targetName) {
              let nextTab = tabs[index + 1] || tabs[index - 1];
              if (nextTab) {
                activeName = nextTab.name;
              }
            }
          });
        }
        this.editableTabsValue = activeName;
        this.editableTabs = tabs.filter(tab => tab.name !== targetName);
        this.editableTabIndex = 0;
        this.editableTabs.forEach(tab => tab.name = this.tabNextName());
      },
      tabNextName(){
        return '第' + (++this.editableTabIndex) + '阶段'
      },
      handleTabsEdit(targetName, action) {
        if (action === 'add') {
          this.tabAdd();
        }
        if (action === 'remove') {
          this.tabRemove(targetName);
        }
      },
      submitForm(){
        let context = this;
        this.$refs['apiForm'].validate((valid) => {
          if(valid){
            context.doSubmitForm();
          }else{
            context.$message.warning("请先完成表单填写")
          }
        });

      },
      doSubmitForm(){
        let context = this;
        let params = JSON.stringify(context.getFormData());
        console.log(context.form, params)
        if(context.form.id){
          update(context.form.id, params).then(response => {
            context.$message.success("操作成功")
            context.$emit('submit')
            context.dialogDisplay = false;
          })
        } else {
          create(params).then(response => {
            context.$message.success("操作成功")
            context.$emit('submit')
            context.dialogDisplay = false;
          })
        }
      },
      getFormData(){
        //将层级结构editableTabs改变为拉平的数据pluginsList TODO
        this.form.pluginList = [
              {
                stage: "stage1",
                stageName: "步骤一",
                stateSn: 1,
                pluginVerId: 3,
                sn: 1,
                configParams: '{"name": "张三", "age": 12}',
              }
            ]
        return this.form;
      },
      resetForm(id, pluginList){
        let plugins = JSON.parse(pluginList);
        this.form.id=id;
        //将拉平的数据plugins改变为层级结构editableTabs TODO

      }
    }
	}
</script>

<style scoped>

</style>
