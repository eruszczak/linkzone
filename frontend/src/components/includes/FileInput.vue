<template>
    <section class="has-text-centered">
        <b-field :label="label" :type="{'is-danger': showError && errors.has('image')}" :message="showError ? errors.first('image') : null">
            <b-upload v-validate="'required'" name="image" v-model="file" drag-drop accept="image/*" @input="onFileChange" :disabled="disabled">
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
        <p v-if="file"><strong><small>{{$t('choosen-file', {name: file.name})}}, {{Math.round(file.size/1000)}}KB</small></strong></p>
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
                file: null,
            }
        },
        watch: {
            showError() {
                this.$validator.validate();
            }
        },
        methods: {
            onFileChange($event) {
                // if ($event.name === this.file.name && $event.size === this.file.size) {
                //     console.log('file not changed. ignoring')
                //     return;
                // }

                if (this.file.size > this.maxSize * 1000) {
                    this.$danger('file-size-error', {size: this.maxSize});
                } else {
                    const form = new FormData();
                    form.append('data', this.file, this.file.name);
                    this.$emit('formData', form);
                }
            }
        }
    }
</script>

<style scoped>
</style>
