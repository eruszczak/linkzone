<template>
    <v-layout justify-center row>
        <v-dialog :transition="null" content-class="loading-dialog" fullscreen persistent v-model="loading">
            <v-container fill-height>
                <v-layout align-center justify-center row>
                    <v-progress-circular :size="70" :width="7" color="purple" indeterminate></v-progress-circular>
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