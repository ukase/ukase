# In-pack helpers
## `set_var` :new:
_since 3.1_<br/>
set some value to current context
* Block mode example:
```
{{#set_var name="varName"}}
  some
  {{#if true}}
    value
  {{else}}
    not value
  {{/if}}
{{/set_var}}
{{varName}} <!-- 'some value' will be printed here -->
```
* Inline mode example:
```
{{set_var "value" name="varName"}}
{{varName}} <!-- 'value' will be printed here -->
```
## `format_number`
## `format_date`
## `if` sub helpers
### `and`
### `or`
### `not`
### `eq`
### `in`
### `ne`
### `lt`
### `lte`
### `gt`
### `gte`
## `substring`