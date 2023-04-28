<template>
  <el-dialog title="插件上传" :visible.sync="dialogDisplay">
    <el-upload
      class="upload-demo"
      drag
      action="https://jsonplaceholder.typicode.com/posts/"
      multiple>
      <i class="el-icon-upload"></i>
      <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
      <div class="el-upload__tip" slot="tip">只能上传jpg/png文件，且不超过500kb</div>
    </el-upload>
  </el-dialog>
</template>

<script>
	export default {
		name: "PluginUpload",
    props: {
      value: {
        type: Boolean,
        default: false
      }
    },
    data(){
      return {
        dialogDisplay: false,
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
      }
    },
	}
</script>

<style scoped>

</style>
