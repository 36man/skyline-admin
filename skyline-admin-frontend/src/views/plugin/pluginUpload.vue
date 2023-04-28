<template>
  <el-dialog title="插件上传" :visible.sync="dialogDisplay">
    <el-upload
      class="upload-demo"
      drag
      action="https://jsonplaceholder.typicode.com/posts/"
      show-upload-list="false"
      name="file"
      :multiple="false"
      :before-upload="beforeUpload"
      :on-success="onSuccess"
    >
      <i class="el-icon-upload"></i>
      <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
      <div class="el-upload__tip" slot="tip">只能上传jar文件，且不超过5M</div>
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
    methods: {
      beforeUpload(file){
        let fileSize = file.size;
        if(fileSize > 1024 * 1024 * 5){
          this.$message.error("文件不能超过5M");
          return false;
        }
        return true;
      },
      onSuccess(response, file, fileList){
        console.log(response, file, fileList)
      }
    }
	}
</script>

<style scoped>

</style>
