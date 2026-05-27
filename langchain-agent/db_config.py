from pathlib import Path
from configparser import ConfigParser

def load_db_config(filename='database.ini', section='postgresql'):
    base_path = Path(__file__).parent.parent
    path_to_file = base_path / filename

    parser = ConfigParser()
    parser.read(str(path_to_file))

    config = {}
    if parser.has_section(section):
        params = parser.items(section)
        for param in params:
            config[param[0]] = param[1]
    else:
        raise Exception(f'Section {section} not found in the {path_to_file} file')
    return config