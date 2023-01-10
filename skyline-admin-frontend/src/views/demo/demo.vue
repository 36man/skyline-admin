<template>
  <div class="login-container">
    <div id="customerForm">
    </div>
    <div>
      <el-button type="primary" @click="onSubmit">获取数据</el-button>
      <el-button @click="add">增加内容</el-button>
    </div>
  </div>
</template>

<script>
  import {getConfig} from "@/api/demo";
  import Vue from 'vue';
  export default {
    name: 'Login',
    data: function() {
      return {
        childForm: null,
        templateContent: ''
      }
    },
    methods: {
      onSubmit() {
        //通过model可以获取到 绑定为model的数据
        console.log(JSON.stringify(this.childForm.__vue__.model));
      },
      add(){
        let context = this;
        getConfig().then(response => {
          context.templateContent = response.data.templateContent;
          context.resetCustomForm()
        })
      },
      resetCustomForm(){
        let tmp = eval("("+this.templateContent+")");
        let customComponent = Vue.extend(tmp);
        let form = new customComponent().$mount().$el;
        document.getElementById("customerForm").appendChild(form);
        this.childForm = form;
      }
    }
  }
</script>
<style lang="scss">
</style>
