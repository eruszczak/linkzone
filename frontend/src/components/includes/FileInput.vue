<template>
    <div>
        <section class="has-text-centered">
            <strong v-if="file">
                {{ file.name }}
            </strong>
            <b-field :type="{'is-danger': showError && errors.has('image')}" :message="showError ? errors.first('image') : null">
                <b-upload name="image" v-model="file" drag-drop accept="image/x-png,image/gif,image/jpeg" @input="onFileChange" :disabled="disabled">
                    <section class="section">
                        <div class="content has-text-centered">
                            <p>
                                <b-icon icon="upload" size="is-large"></b-icon>
                            </p>
                            <p>{{'drop-image' | t }}</p>
                        </div>
                    </section>
                </b-upload>
            </b-field>
        </section>
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
            // maxWidth: {
            //     type: Number,
            //     default: 100
            // },
            // maxHeight: {
            //     type: Number,
            //     default: 100
            // },
            isImage: {
                type: Boolean,
                default: false
            },
            showError: {
                type: Boolean,
                default: false
            }
        },
        data() {
            return {
                filename: '',
                errors1: [],
                dropFiles: null,
                file: null
            }
        },
        watch: {
            value(v) {
                this.filename = v
            }
        },
        mounted() {
            this.filename = this.value
        },
        methods: {
            onFileChange($event) {
                this.errors1 = [];
                const forms = [];
                console.log(this.file.name)
                if (this.file.size > this.maxSizeInKB * 1000) {
                    this.errors1.push(`File is igger than ${this.maxSizeInKB} KB`);
                    this.filename = '';
                } else {
                    const form = new FormData();
                    form.append('data', this.file, this.file.name);
                    forms.push(form);
                }
                this.$emit('input', this.filename);
                this.$emit('formData', {
                    value: forms,
                    errors: this.errors1
                })
            }
        }
    }
</script>

<style scoped>
    /* input[type=file] {
        position: absolute;
        left: -99999px;
    } */
</style>
