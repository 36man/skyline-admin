<template>
  <el-dialog title="插件信息修改" :visible.sync="dialogDisplay">
    <el-form :model="form" ref="clusterForm">
      <el-form-item label="插件名称" :label-width="formLabelWidth" prop="pluginName" :rules="[
      { required: true, message: '请输入插件名称', trigger: 'blur' }, { min: 1, max: 50, message: '长度在 1 到 50 个字符', trigger: 'blur' }]">
        <el-input size="small" v-model="form.pluginName" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="作者" :label-width="formLabelWidth" prop="maintainer" :rules="[
      { required: true, message: '请输入作者', trigger: 'blur' }, { min: 1, max: 50, message: '长度在 1 到 50 个字符', trigger: 'blur' }]">
        <el-input size="small" v-model="form.maintainer" autocomplete="off"></el-input>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogDisplay = false">取 消</el-button>
      <el-button type="primary" @click="submitForm">确 定</el-button>
    </div>
  </el-dialog>
</template>

<script>
  import {update} from '@/api/plugin'
	export default {
		name: "PluginInfoForm",
    props: {
		  value: {
        type: Boolean,
        default: false
      },
		  rowData: {
        type: Object,
        default: {}
      }
    },
    data(){
		  return {
        form: {},
        dialogDisplay: false,
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
      rowData(newValue) {
        this.form = newValue
      },
    },
    methods: {
      submitForm(){
        let context = this;
        update(context.form).then(() => {
          context.$message.success("操作成功")
          context.$emit('submit')
          context.dialogDisplay = false;
        })
      },
    }
	}
</script>

<style scoped>

</style>
