/* eslint-disable */
<template>
  <el-dialog title="api配置" :visible.sync="dialogDisplay">
    <el-form :model="form" ref="apiForm">
      <el-form-item label="匹配规则" :label-width="formLabelWidth" prop="matchCondition" :rules="[
      { required: true, message: '请输入匹配规则', trigger: 'blur' }, { min: 1, max: 50, message: '长度在 1 到 50 个字符', trigger: 'blur' }]">
        <el-input size="small" v-model="form.matchCondition" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="api描述" :label-width="formLabelWidth" prop="description" :rules="[
      { required: true, message: '请输入api描述', trigger: 'blur' }, { min: 1, max: 100, message: '长度在 1 到 100 个字符', trigger: 'blur' }]">
        <el-input size="small" v-model="form.description" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="备注" :label-width="formLabelWidth" prop="meno" :rules="[
      { required: true, message: '请输入备注', trigger: 'blur' }, { min: 1, max: 100, message: '长度在 1 到 100 个字符', trigger: 'blur' }]">
        <el-input size="small" v-model="form.meno" autocomplete="off"></el-input>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogDisplay = false">取 消</el-button>
      <el-button type="primary" @click="submitForm">确 定</el-button>
    </div>
  </el-dialog>
</template>

<script>
  import { create,update } from '@/api/api'
	export default {
		name: "apiForm",
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
        form: {},
        formLabelWidth: '120px',
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
        this.form = newValue
      },
    },
    methods: {
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
        if(context.form.id){
          update(context.form.id, context.form).then(response => {
            context.$message.success("操作成功")
            context.$emit('submit')
            context.dialogDisplay = false;
          })
        } else {
          create(context.form).then(response => {
            context.$message.success("操作成功")
            context.$emit('submit')
            context.dialogDisplay = false;
          })
        }
      },
    }
	}
</script>

<style scoped>

</style>
