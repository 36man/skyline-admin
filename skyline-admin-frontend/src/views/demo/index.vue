<template>
  <div class="login-container">
  <display :code="code" ref="display"></display>
  <div>
    <el-button type="primary" @click="onSubmit">获取数据</el-button>
    <el-button @click="add">增加内容</el-button>
  </div>
  </div>
</template>
<script>
  import Display from '@/components/DynamicDisplay/display';
  import {getConfig} from "@/api/demo";

  export default {
    components: {Display},
    data() {
      return {
        code: '',
      }
    },
    methods: {
      onSubmit() {
        let data = this.$refs.display.getFormData();
        if(data != null){
          console.log(data);
        }
      },
      add(){
        let context = this;
        getConfig().then(response => {
          context.code = response.data.templateContent;
        })
      }
    }
  }
</script>
