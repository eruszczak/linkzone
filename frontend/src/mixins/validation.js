export default {
    methods: {
        ruleIsNotEmpty(val) {
            return !!val || 'value_required'
        },
        ruleUrl(val) {
            if (val) {
                return isUrlValid(val)
            }
            return true
        },
        ruleEmail(val) {
            if (val) {
                const re = /\S+@\S+\.\S+/;
                return re.test(val);
            }
            return true
        }
    }
}

const isUrlValid = (string) => {
    try {
        new URL(string);
        return true;
    } catch (_) {
        return false;
    }
}