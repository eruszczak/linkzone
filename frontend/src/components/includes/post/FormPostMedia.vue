<template>
    <v-card-text>
        <!--<el-upload-->
        <!--class="upload-demo"-->
        <!--drag-->
        <!--action="https://jsonplaceholder.typicode.com/posts/"-->
        <!--:file-list="formMedia.fileList">-->
        <!--<i class="el-icon-upload"></i>-->
        <!--<div class="el-upload__text">Drop file here or <em>click to upload</em></div>-->
        <!--<div class="el-upload__tip" slot="tip">jpg/png files with a size less than 500kb</div>-->
        <!--</el-upload>-->
        <!-- {{formCopy.uploadResult.errors}} -->
        <v-text-field
                label="Title"
                :rules="[ruleIsNotEmpty]"
                box
                v-model="formCopy.title"
        ></v-text-field>
        <file-input v-model="formCopy.filename" @formData="handleFormData" :is-image="true" :current-image-url="form.filename ? '/static/' + form.filename : ''" show-picked-image></file-input>
    </v-card-text>
</template>

<script>
    import FileInput from '../FileInput';
    import validation from '../../../mixins/validation'

    export default {
        name: "FormPostMedia",
        mixins: [validation],
        components: {FileInput},
        props: {
          form: {
            type: Object,
            required: true
          }
        },
        watch: {
            formCopy: {
                handler(val) {
                    console.log('emitting', val)
                    this.$emit('change', val)
                },
                deep: true
            },
            form: {
                handler(val) {
                    this.formCopy = {...val}
                },
                deep: true
            }
        },
        data () {
          return {
            formCopy: {...this.form},
            uploadResult: {}
          }
        },
        methods: {
          handleFormData(val) {
            console.log(val)
            this.formCopy.uploadResult = val
            this.formCopy.valid = val.errors.length === 0
          }
        }
    }
</script>

<style scoped>

</style>
