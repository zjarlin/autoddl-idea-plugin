# AutoDDL Plugin for IntelliJ IDEA

The **AutoDDL** plugin helps you effortlessly generate `CREATE TABLE` statements within IntelliJ IDEA. Users can either rely on the power of large language models (LLMs) to initially generate structured form metadata (hereinafter referred to as **Form Cell Metadata J**) or manually write forms to generate DDL statements.

## Features
- **Effortless Table Creation**: Automatically generate `CREATE TABLE` SQL statements based on structured form metadata.
- **LLM Support**: Users can optionally use large language models to automatically generate the structured form for creating tables.
- **Java Type Mapping**: No need to worry about database column types. The plugin maps Java types to database types and generates the corresponding DDL statements.
- **Manual Editing**: Users can still edit the generated form metadata **J** for further customization of the DDL.
- **Manual Form Writing**: For users who prefer manual control, it's also possible to write forms directly and generate DDL without invoking the LLM capabilities.

## Usage
1. **Plugin Entry**: Find the plugin under the **Tools** menu -> **AutoDDL** in IntelliJ IDEA.
2. **Initial Setup**: Before using the plugin, you need to configure the API Key for the LLM:
    - Currently, only **Alibaba Lingji** (`DASHSCOPE_API_KEY`) is supported.
    - You can configure your API Key in **IDEA Settings**.
3. **Invoking LLM for Table Creation**:
    - Choose to leverage the LLM to generate structured form metadata for table creation.
    - After generation, you can still make manual edits to the form if necessary.
4. **Manual Form Writing**:
    - If preferred, users can manually write the forms to generate DDL statements without relying on the LLM.