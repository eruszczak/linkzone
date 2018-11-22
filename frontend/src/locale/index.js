import pl from "./pl";
import en from "./en";

export const AVAILABLE_LANGUAGES = [
    {
        'name': 'English',
        'icon': 'flags/usa.png',
        'locale': 'en'
    },
    {
        'name': 'Polski',
        'icon': 'flags/poland.png',
        'locale': 'pl'
    }
]

export default {
    pl: pl,
    en: en
}