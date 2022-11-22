const Validation = {
    isMinLength: (string, min) => {
        return String(string).length >= min;
    },
    isMaxLength: (string, max) => {
        return String(string).length <= max;
    }
};
export default Validation;