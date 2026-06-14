# jupyterhub stack module

- Module id: `jupyterhub`
- Module repo: `jupyterhub-stack-module`
- Source repo: none declared
- Lifecycle: `active`

## Owned overlays
- `stack.compose/jupyterhub.yml`
- `stack.config/jupyterhub`
- `stack.containers/jupyterhub`

## Dependencies
- `stack-foundation`

## Validation

```sh
./tests/validate.sh
```

## Lifecycle

`active` modules are expected to keep `stack.module.json`, owned overlays, and `tests/validate.sh` in sync.
