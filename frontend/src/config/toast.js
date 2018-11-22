import {TOAST_COLORS} from "../utils/utils";
import {store} from "../store";

export const toast = (config, cb) => {
    const options = {
        timeout: config.seconds ? config.seconds * 1000 : 1000,
        message: config.message,
        color: config.color || TOAST_COLORS.INFO
    }
    console.log('toast options', options)
    if (options.message) {
        store.commit('setShowToast', options)
        setTimeout(() => {
            store.commit('hideToast')
            cb && cb()
        }, options.timeout - 500)
    }
}