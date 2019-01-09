<template>
    <transition name="fade">
        <img style="width:auto;height:auto" :src="src" @load="onLoaded" v-show="loaded">
                <!-- <img class="icon" v-bind:src="src" v-on:load="onLoaded" v-show="loaded">&nbsp; -->
    </transition>
</template>

<script>
    export default {
        props: {
            src: {
                type: String,
                required: true
            }
        },
        data() {
            return {
                loaded: false
            }
        },
        methods: {
            onLoaded() {
                this.loaded = true;
            }
        }
    }
</script>

<style scoped>
</style>
