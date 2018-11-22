<template>
    <v-layout row justify-center>
        <v-dialog v-model="loading" persistent fullscreen content-class="loading-dialog" :transition="null">
            <v-container fill-height>
                <v-layout row justify-center align-center>
                    <v-progress-circular indeterminate :size="70" :width="7" color="purple"></v-progress-circular>
                </v-layout>
            </v-container>
        </v-dialog>
    </v-layout>
</template>

<script>
    export default {
        name: 'Loading',
        props: ['loading']
    }
</script>

<style>
    .loading-dialog {
        background-color: grey;
        opacity: 0.1;
    }
</style>