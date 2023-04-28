export function parseObj2KVArray(obj){
  if(!(obj instanceof Object))return [];
  let arr = [];
  try{
    for (let objKey in obj) {
      if(obj.hasOwnProperty(objKey)){
        arr.push({key: objKey, value: obj[objKey] + ""})
      }
    }
  }catch (e) {
    console.error("object convert to array error：", e)
  }

  return arr;
}

export function parseKVAarray2Obj(kvArray) {
  if(!(kvArray instanceof Array))return {};
  let obj = {};
  try{
    for (let i = 0; i < kvArray.length; i++) {
      let item = kvArray[i];
      if(item.key){
        obj[item.key] = item.value;
      }
    }
  }catch (e) {
    console.error("array convert to object error：", e)
  }

  return obj;
}

export function isJson(str) {
  if (typeof str == 'string') {
    try {
      var obj=JSON.parse(str);
      if(typeof obj == 'object' && obj ){
        return true;
      }else{
        return false;
      }

    } catch(e) {
      return false;
    }
  }else if (typeof str == 'object'  && str) {
    return true;
  }
}
