import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';
import en from './locales/en.json';
import fr from './locales/fr.json';

// Bilingual from day one (Dev Principle 12): every string is a key in en.json and fr.json.
// Interpolation ({{step}}) replaces the prototype's text-node matching, which broke on mixed text.
void i18n.use(initReactI18next).init({
  resources: { en: { translation: en }, fr: { translation: fr } },
  lng: 'en',
  fallbackLng: 'en',
  interpolation: { escapeValue: false },
});

export default i18n;
