<template>
    <section class="has-text-centered">
        <b-field :label="label" :type="{'is-danger': showError && errors.has('image')}" :message="showError ? errors.first('image') : null">
            <b-upload v-validate="'required'" name="image" v-model="file" drag-drop accept="image/x-png,image/gif,image/jpeg" @input="onFileChange" :disabled="disabled">
                <section class="" style="padding: 1rem 1.5rem">
                    <div class="content has-text-centered">
                        <p>
                            <b-icon icon="upload" size="is-large"></b-icon>
                        </p>
                        <p>{{'drop-image' | t }}</p>
                    </div>
                </section>
            </b-upload>
        </b-field>
        <p><small>{{$t('file-size', {size: maxSize})}}</small></p>
        <p><small>{{$t('file-restrictions', {width: maxWidth, height: maxHeight})}}</small></p>
        <p v-if="file"><small>{{$t('choosen-file', {name: file.name})}}</small></p>
    </section>
</template>

<script>
    export default {
        props: {
            label: {
                type: String,
                default: ''
            },
            disabled: {
                type: Boolean,
                default: false
            },
            maxSize: {
                type: Number,
                default: 500
            },
            maxWidth: {
                type: Number,
                default: 100
            },
            maxHeight: {
                type: Number,
                default: 100
            },
            showError: {
                type: Boolean,
                default: false
            }
        },
        data() {
            return {
                filename: '',
                file: null,
                previousFile: null
            }
        },
        watch: {
            showError() {
                this.$validator.validate();
            }
        },
        methods: {
            onFileChange($event) {
                console.log('event', $event)
                if (!$event) {
                    console.log('no file')
                    return;
                }
                console.log($event.name, $event.size)
                console.log(this.file.name, this.file.size)
                // if (this.previousFile && this.previousFile.size === this.file.size) {
                //     console.log('not changed')
                //     // file not changed, someone picked a file then opened file dialog and cancelled it
                //     return;
                // }
                // this.previousFile = this.file;


                // if (this.file.size > this.maxSize * 1000) {
                //     this.$toast.open({
                //         message: this.$t('file-size-error', {size: this.maxSize}),
                //         type: 'is-danger'
                //     });
                //     this.filename = '';
                // } else {
                //     const form = new FormData();
                //     form.append('data', this.file, this.file.name);
                //     this.$emit('formData', form);
                // }


                // this.$emit('input', this.filename);
            }
        }
    }
</script>

<style scoped>
</style>
