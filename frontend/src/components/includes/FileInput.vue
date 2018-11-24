<template>
    <div>
        <p>
            Current:
            <img v-if="currentImageUrl" :src="currentImageUrl" style="max-width:100%">
        </p>
        <v-text-field prepend-icon="attach_file" single-line
                      v-model="filename" :label="$t(label).toUpperCase()" :required="required"
                      @click.native="onFocus"
                      :disabled="disabled" ref="fileTextField"></v-text-field>
        <input type="file" :accept="isImage ? 'image/x-png,image/gif,image/jpeg' : accept" :multiple="multiple" :disabled="disabled"
               ref="fileInput" @change="onFileChange">
    </div>
</template>

<script>
    export default {
        props: {
            value: {
                type: [Array, String]
            },
            accept: {
                type: String,
                default: '*'
            },
            label: {
                type: String,
                default: 'choose_file'
            },
            required: {
                type: Boolean,
                default: false
            },
            disabled: {
                type: Boolean,
                default: false
            },
            multiple: {
                type: Boolean,
                default: false
            },
            maxSizeInKB: {
                type: Number,
                default: 500
            },
            isImage: {
                type: Boolean,
                default: false
            },
            currentImageUrl: {
                type: String,
                default: ''
            }
        },
        data () {
            return {
                filename: '',
                errors: []
            }
        },
        watch: {
            value (v) {
                this.filename = v
            }
        },
        mounted () {
            this.filename = this.value
        },
        methods: {
            getFormData (files) {
                const forms = []
                this.errors = []
                for (const file of files) {
                    console.log('fileInput', file.size)
                    if (file.size > this.maxSizeInKB * 1000) {
                        this.errors.push(`${file.name} is bigger than ${this.maxSizeInKB} KB`);
                        continue;
                    }
                    const form = new FormData()
                    form.append('data', file, file.name)
                    forms.push(form)
                }
                return forms
            },
            onFocus () {
                if (!this.disabled) {
                    this.$refs.fileInput.click()
                }
            },
            onFileChange ($event) {
                const files = $event.target.files || $event.dataTransfer.files
                const form = this.getFormData(files)
                if (files) {
                    if (files.length > 0) {
                        this.filename = [...files].map(file => file.name).join(', ')
                    } else {
                        this.filename = null
                    }
                } else {
                    this.filename = $event.target.value.split('\\').pop()
                }
                console.error('form', form)
                this.$emit('input', this.filename)
                this.$emit('formData', {
                    value: form,
                    errors: this.errors
                })
            }
        }
    }
</script>

<style scoped>
    input[type=file] {
        position: absolute;
        left: -99999px;
    }
</style>
