const crypto = require('crypto');

const createSha256Hash = (input) => {
    if (input === undefined || input === null) {
        return '';
    }

    return crypto.createHash('sha256').update(input).digest('hex');
};

module.exports = {
    createSha256Hash
};
