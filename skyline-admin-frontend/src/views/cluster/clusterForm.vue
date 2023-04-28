/* eslint-disable */
<template>
  <el-dialog title="集群配置" :visible.sync="dialogDisplay">
    <el-form :model="form" ref="clusterForm">
      <el-form-item label="集群名称" :label-width="formLabelWidth" prop="clusterName" :rules="[
      { required: true, message: '请输入集群名称', trigger: 'blur' }, { min: 1, max: 50, message: '长度在 1 到 50 个字符', trigger: 'blur' }]">
        <el-input size="small" v-model="form.clusterName" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="所属业务" :label-width="formLabelWidth" prop="bizKey" :rules="[
      { required: true, message: '请输入所属业务', trigger: 'blur' }, { min: 1, max: 50, message: '长度在 1 到 50 个字符', trigger: 'blur' }]">
        <el-input size="small" v-model="form.bizKey" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="服务地址" :label-width="formLabelWidth" prop="domain" :rules="[
      { required: true, message: '请输入服务地址', trigger: 'blur' }, { min: 1, max: 50, message: '长度在 1 到 50 个字符', trigger: 'blur' }]">
        <el-input size="small" v-model="form.domain" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="服务实例数" :label-width="formLabelWidth"prop="instanceCount" :rules="[{ required: true, message: '请输入服务实例数', trigger: 'blur' }]">
        <el-input-number size="small" v-model="form.instanceCount" :min="1" :max="100"></el-input-number>
      </el-form-item>
      <el-form-item label="服务规格" :label-width="formLabelWidth"prop="useQuota" :rules="[
      { required: true, message: '请输入服务规格', trigger: 'blur' }, { validator: checkQuota, trigger: 'blur' }]">
        <el-input size="small" v-model="form.useQuota" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="共用配置中心" :label-width="formLabelWidth">
        <el-switch v-model="form.configShare" active-text="是" inactive-text="否"></el-switch>
      </el-form-item>
      <el-form-item label="配置中心地址" :label-width="formLabelWidth" prop="configUrl" :rules="[
      { required: true, message: '请输入配置中心地址', trigger: 'blur' }, { min: 1, max: 200, message: '长度在 1 到 200 个字符', trigger: 'blur' }]">
        <el-input size="small" v-model="form.configUrl" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="配置中心账号" :label-width="formLabelWidth" prop="configUser" :rules="[
      { required: true, message: '请输入配置中心账号', trigger: 'blur' }, { min: 1, max: 50, message: '长度在 1 到 50 个字符', trigger: 'blur' }]">
        <el-input size="small"t v-model="form.configUser" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="配置中心密码" :label-width="formLabelWidth">
        <el-input size="small" v-model="form.configSecret" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="备注" :label-width="formLabelWidth" prop="meno" :rules="[
      { required: false, message: '请输入备注', trigger: 'blur' }, { min: 0, max: 100, message: '长度在 0 到 100 个字符', trigger: 'blur' }]">
        <el-input size="small" v-model="form.meno" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="其他配置" :label-width="formLabelWidth">
        <el-row style="line-height: 20px;">
          <b-code-editor v-model="configItemJson" theme="material" :lint="true" :show-number="true" :readonly="false" :indent-unit="4" :line-wrap="true" ref="editor" />
        </el-row>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogDisplay = false">取 消</el-button>
      <el-button type="primary" @click="submitForm">确 定</el-button>
    </div>
  </el-dialog>
</template>

<script>
  import { findById, create, update } from '@/api/cluster'
  import { isJson } from '@/utils/jsons'
	export default {
		name: "ClusterForm",
    props: {
      value: {
        type: Boolean,
        default: false
      },
      id: {
        type: String,
        default: ''
      }
    },
    data(){
      return {
        dialogDisplay: false,
        form: {},
        configItemJson: '{}',
        isEdit: false,
        formLabelWidth: '120px',
        checkQuota: (rule, value, callback) => {
          if (/^[1-9][0-9]{0,3}c[1-9][0-9]{0,3}g/i.test(value)) {
            callback();
          } else {
            callback(new Error('服务规格格式为[1-9999]c[1-9999]g'));
          }
        }
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
      id() {
        this.fetchData()
      },
    },
    methods: {
      fetchData() {
        if(this.id){
          this.isEdit = true;
          let context = this;
          findById(context.id).then(response => {
            context.form = response.data
            context.configItemJson = JSON.stringify(context.form.configItem)
            context.$nextTick(()=>{
              context.$refs["editor"].formatCode()
            })
          })
        } else {
          this.isEdit = false;
          this.form = {};
          this.configItemJson = "{}"
        }
      },
      submitForm(){
        let context = this;
        this.$refs['clusterForm'].validate((valid) => {
          if(valid){
            if(!isJson(this.configItemJson)){
              context.$message.warning("json格式不正确")
            } else {
              context.doSubmitForm();
            }
          }else{
            context.$message.warning("请先完成表单填写")
          }
        });

      },
      doSubmitForm(){
        let context = this;
        if(context.isEdit){
          let params = this.getFormData()
          update(context.id, params).then(response => {
            context.$message.success("操作成功")
            context.$emit('submit')
            context.dialogDisplay = false;
          })
        } else {
          let params = this.getFormData()
          create(params).then(response => {
            context.$message.success("操作成功")
            context.$emit('submit')
            context.dialogDisplay = false;
          })
        }
      },
      getFormData(){
        this.form.configItem = JSON.parse(this.configItemJson)
        return this.form;
      }
    }
	}
</script>

<style scoped>

</style>
