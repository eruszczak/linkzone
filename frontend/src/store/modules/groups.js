export default {
    state: {
        groups: [],
        filteredGroups: null,
        filteredGroups2: null
    },
    getters: {
        groups: state => state.groups,
        filteredGroups: state => {
            if (state.filteredGroups === null) {
                return state.groups
            }
            return state.filteredGroups
        },
        filteredGroups2: state => {
            if (state.filteredGroups2 === null) {
                return state.groups
            }
            return state.filteredGroups2
        }
    },
    mutations: {
        unsubGroup: (state, value) => {
            state.groups = state.groups.filter(group => group.name !== value.name)
        },
        addGroup: (state, value) => {
            // if (state.groups.filter(g => g.id === value.id).length === 0) {
            state.groups.push(value)
            // }
        },
        setGroups: (state, value) => {
            state.groups = value
        },
        setFilterGroups: (state, value) => {
            state.filteredGroups = value
        },
        filterGroups: (state, value) => {
            if (!value) {
                state.filteredGroups = state.groups.slice()
            }
            state.filteredGroups = state.groups.filter(g => g.name.toLowerCase().indexOf(value.toLowerCase()) > -1)
        },
        filterGroups2: (state, value) => {
            console.log('filterGroups2', value)
            if (!value) {
                console.log('returning all groups')
                state.filteredGroups2 = state.groups.slice()
            }
            state.filteredGroups2 = state.groups.filter(g => g.name.toLowerCase().indexOf(value.toLowerCase()) > -1)
        },

    },
    actions: {
        deleteGroup: ({commit, getters}, groupName) => {
            commit('setFilterGroups', getters.filteredGroups.filter(g => g.name.toLowerCase().indexOf(groupName.toLowerCase()) === -1))
            commit('setGroups', getters.groups.filter(g => g.name.toLowerCase().indexOf(groupName.toLowerCase()) === -1))
        }
    }
}
